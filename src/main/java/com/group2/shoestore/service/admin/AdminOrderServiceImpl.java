package com.group2.shoestore.service.admin;

import com.group2.shoestore.dto.response.OrderDetailResponse;
import com.group2.shoestore.dto.response.OrderResponse;
import com.group2.shoestore.entity.Order;
import com.group2.shoestore.entity.OrderItem;
import com.group2.shoestore.exception.BadRequestException;
import com.group2.shoestore.exception.ResourceNotFoundException;
import com.group2.shoestore.repository.OrderItemRepository;
import com.group2.shoestore.repository.OrderRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.stream.Collectors;

/**
 * Service implement cho quản lý đơn hàng
 */
@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class AdminOrderServiceImpl implements AdminOrderService {

    private final OrderRepository orderRepository;
    private final OrderItemRepository orderItemRepository;

    /**
     * Tìm kiếm đơn hàng với bộ lọc và phân trang
     */
    @Override
    @Transactional(readOnly = true)
    public Page<OrderResponse> search(String orderCode, String customerName, String status, Pageable pageable) {
        log.info("Searching orders - orderCode: {}, customerName: {}, status: {}", orderCode, customerName, status);
        // CHUẨN HÓA DỮ LIỆU Ở ĐÂY
        // Nếu rỗng hoặc chỉ toàn khoảng trắng thì biến thành null
        String cleanCode = (orderCode != null && !orderCode.isBlank()) ? orderCode.trim() : null;
        String cleanName = (customerName != null && !customerName.isBlank()) ? customerName.trim() : null;
        String cleanStatus = (status != null && !status.isBlank()) ? status.trim() : null;
        try {
            //Page<Order> orders = orderRepository.search(orderCode, customerName, status, pageable);
            Page<Order> orders = orderRepository.search(cleanCode, cleanName, cleanStatus, pageable);
            return orders.map(this::convertToResponse);
        } catch (Exception e) {
            log.error("Error searching orders", e);
            throw new RuntimeException("Lỗi khi tìm kiếm đơn hàng: " + e.getMessage());
        }
    }

    /**
     * Lấy thông tin chi tiết đơn hàng theo ID
     */
    @Override
    @Transactional(readOnly = true)
    public OrderDetailResponse getOrderById(Long id) {
        log.info("Getting order by id: {}", id);
        
        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Đơn hàng không tồn tại với ID: " + id));
        
        return convertToDetailResponse(order);
    }

    /**
     * Cập nhật trạng thái đơn hàng
     */
    @Override
    public OrderDetailResponse updateOrderStatus(Long id, String newStatus) {
        log.info("Updating order status - id: {}, newStatus: {}", id, newStatus);
        
        // Lấy đơn hàng từ database
        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Đơn hàng không tồn tại với ID: " + id));
        
        // Kiểm tra trạng thái hiện tại
        String currentStatus = order.getOrderStatus();
        
        // Validate trạng thái mới
        validateStatusTransition(currentStatus, newStatus);
        
        try {
            // Cập nhật trạng thái
            order.setOrderStatus(newStatus);
            Order updatedOrder = orderRepository.save(order);
            log.info("Order status updated successfully - id: {}, newStatus: {}", id, newStatus);
            
            return convertToDetailResponse(updatedOrder);
        } catch (Exception e) {
            log.error("Error updating order status", e);
            throw new RuntimeException("Lỗi khi cập nhật trạng thái đơn hàng: " + e.getMessage());
        }
    }

    /**
     * Hủy đơn hàng
     */
    @Override
    public void cancelOrder(Long id) {
        log.info("Cancelling order with id: {}", id);
        
        // Lấy đơn hàng từ database
        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Đơn hàng không tồn tại với ID: " + id));
        
        // Kiểm tra xem đơn hàng có thể hủy được không
        if ("DELIVERED".equals(order.getOrderStatus()) || "CANCELLED".equals(order.getOrderStatus())) {
            throw new BadRequestException("Không thể hủy đơn hàng có trạng thái: " + order.getOrderStatus());
        }
        
        try {
            // Cập nhật trạng thái thành CANCELLED
            order.setOrderStatus("CANCELLED");
            orderRepository.save(order);
            log.info("Order cancelled successfully - id: {}", id);
        } catch (Exception e) {
            log.error("Error cancelling order", e);
            throw new RuntimeException("Lỗi khi hủy đơn hàng: " + e.getMessage());
        }
    }

    /**
     * Lấy danh sách đơn hàng theo trạng thái
     */
    @Override
    @Transactional(readOnly = true)
    public Page<OrderResponse> getOrdersByStatus(String status, Pageable pageable) {
        log.info("Getting orders by status: {}", status);
        
        try {
            Page<Order> orders = orderRepository.findByOrderStatusOrderByCreatedAtDesc(status, pageable);
            return orders.map(this::convertToResponse);
        } catch (Exception e) {
            log.error("Error getting orders by status", e);
            throw new RuntimeException("Lỗi khi lấy danh sách đơn hàng: " + e.getMessage());
        }
    }

    /**
     * Kiểm tra xem chuyển đổi trạng thái có hợp lệ không
     */
    private void validateStatusTransition(String currentStatus, String newStatus) {
        // Những trạng thái hợp lệ: CONFIRMED, SHIPPING, DELIVERED, CANCELLED
        if (!isValidStatus(newStatus)) {
            throw new BadRequestException("Trạng thái đơn hàng không hợp lệ: " + newStatus);
        }
        
        // Không thể chuyển từ DELIVERED hoặc CANCELLED sang trạng thái khác
        if (("DELIVERED".equals(currentStatus) || "CANCELLED".equals(currentStatus)) 
            && !currentStatus.equals(newStatus)) {
            throw new BadRequestException("Không thể thay đổi trạng thái từ " + currentStatus);
        }
    }

    /**
     * Kiểm tra xem trạng thái có hợp lệ không
     */
    private boolean isValidStatus(String status) {
        return "CONFIRMED".equals(status) || "SHIPPING".equals(status) || 
               "DELIVERED".equals(status) || "CANCELLED".equals(status);
    }

    /**
     * Chuyển đổi Order entity sang OrderResponse DTO
     */
    private OrderResponse convertToResponse(Order order) {
        long totalItems = orderItemRepository.countByOrderId(order.getId());
        
        return OrderResponse.builder()
                .id(order.getId())
                .orderCode(order.getOrderCode())
                .receiverName(order.getReceiverName())
                .receiverPhone(order.getReceiverPhone())
                .finalAmount(order.getFinalAmount())
                .paymentMethod(order.getPaymentMethod())
                .paymentStatus(order.getPaymentStatus())
                .orderStatus(order.getOrderStatus())
                .createdAt(order.getCreatedAt())
                .updatedAt(order.getUpdatedAt())
                .totalItems((int) totalItems)
                .build();
    }

    /**
     * Chuyển đổi Order entity sang OrderDetailResponse DTO
     */
    private OrderDetailResponse convertToDetailResponse(Order order) {
        var orderItems = orderItemRepository.findByOrderId(order.getId());
        
        return OrderDetailResponse.builder()
                .id(order.getId())
                .orderCode(order.getOrderCode())
                .userId(order.getUser().getId())
                .userName(order.getUser().getUsername())
                .receiverName(order.getReceiverName())
                .receiverPhone(order.getReceiverPhone())
                .shippingAddress(order.getShippingAddress())
                .paymentMethod(order.getPaymentMethod())
                .paymentStatus(order.getPaymentStatus())
                .orderStatus(order.getOrderStatus())
                .note(order.getNote())
                .totalAmount(order.getTotalAmount())
                .finalAmount(order.getFinalAmount())
                .orderItems(orderItems.stream()
                        .map(this::convertOrderItemToDetail)
                        .collect(Collectors.toList()))
                .createdAt(order.getCreatedAt())
                .updatedAt(order.getUpdatedAt())
                .build();
    }

    /**
     * Chuyển đổi OrderItem entity sang OrderItemDetail DTO
     */
    private OrderDetailResponse.OrderItemDetail convertOrderItemToDetail(OrderItem item) {
        return OrderDetailResponse.OrderItemDetail.builder()
                .id(item.getId())
                .productName(item.getProductName())
                .color(item.getColor())
                .size(item.getSize())
                .unitPrice(item.getUnitPrice())
                .quantity(item.getQuantity())
                .subtotal(item.getSubtotal())
                .build();
    }
}
