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

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class AdminOrderServiceImpl implements AdminOrderService {

    private static final String PAYMENT_METHOD_COD = "COD";
    private static final String PAYMENT_METHOD_BANK_QR = "BANK_QR";
    private static final String PAYMENT_STATUS_PENDING = "PENDING";
    private static final String PAYMENT_STATUS_PAID = "PAID";
    private static final String ORDER_STATUS_PENDING_PAYMENT = "PENDING_PAYMENT";
    private static final String ORDER_STATUS_CONFIRMED = "CONFIRMED";
    private static final String ORDER_STATUS_COMPLETED = "COMPLETED";
    private static final String ORDER_STATUS_CANCELLED = "CANCELLED";

    private final OrderRepository orderRepository;
    private final OrderItemRepository orderItemRepository;

    @Override
    @Transactional(readOnly = true)
    public Page<OrderResponse> search(String orderCode, String customerName, String status, Pageable pageable) {
        log.info("Searching orders - orderCode: {}, customerName: {}, status: {}", orderCode, customerName, status);

        String cleanCode = (orderCode != null && !orderCode.isBlank()) ? orderCode.trim() : null;
        String cleanName = (customerName != null && !customerName.isBlank()) ? customerName.trim() : null;
        String cleanStatus = (status != null && !status.isBlank()) ? status.trim() : null;

        try {
            Page<Order> orders = orderRepository.search(cleanCode, cleanName, cleanStatus, pageable);
            return orders.map(this::convertToResponse);
        } catch (Exception e) {
            log.error("Error searching orders", e);
            throw new RuntimeException("Lỗi khi tìm kiếm đơn hàng: " + e.getMessage());
        }
    }

    @Override
    @Transactional(readOnly = true)
    public OrderDetailResponse getOrderById(Long id) {
        log.info("Getting order by id: {}", id);

        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Đơn hàng không tồn tại với ID: " + id));

        return convertToDetailResponse(order);
    }

    @Override
    public OrderDetailResponse updateOrderStatus(Long id, String newStatus) {
        log.info("Updating order status - id: {}, newStatus: {}", id, newStatus);

        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Đơn hàng không tồn tại với ID: " + id));

        String currentStatus = order.getOrderStatus();
        validateStatusTransition(currentStatus, newStatus);

        try {
            order.setOrderStatus(newStatus);
            Order updatedOrder = orderRepository.save(order);
            log.info("Order status updated successfully - id: {}, newStatus: {}", id, newStatus);
            return convertToDetailResponse(updatedOrder);
        } catch (Exception e) {
            log.error("Error updating order status", e);
            throw new RuntimeException("Lỗi khi cập nhật trạng thái đơn hàng: " + e.getMessage());
        }
    }

    @Override
    public void cancelOrder(Long id) {
        log.info("Cancelling order with id: {}", id);

        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Đơn hàng không tồn tại với ID: " + id));

        if (!isCancellableStatus(order.getOrderStatus())) {
            throw new BadRequestException("Không thể hủy đơn hàng có trạng thái: " + resolveOrderStatusText(order.getOrderStatus()));
        }

        try {
            order.setOrderStatus(ORDER_STATUS_CANCELLED);
            orderRepository.save(order);
            log.info("Order cancelled successfully - id: {}", id);
        } catch (Exception e) {
            log.error("Error cancelling order", e);
            throw new RuntimeException("Lỗi khi hủy đơn hàng: " + e.getMessage());
        }
    }

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

    private void validateStatusTransition(String currentStatus, String newStatus) {
        if (!isValidStatus(newStatus)) {
            throw new BadRequestException("Trạng thái đơn hàng không hợp lệ: " + newStatus);
        }

        if ((ORDER_STATUS_COMPLETED.equals(currentStatus) || ORDER_STATUS_CANCELLED.equals(currentStatus))
                && !currentStatus.equals(newStatus)) {
            throw new BadRequestException("Không thể thay đổi trạng thái từ " + resolveOrderStatusText(currentStatus));
        }
    }

    private boolean isValidStatus(String status) {
        return ORDER_STATUS_PENDING_PAYMENT.equals(status)
                || ORDER_STATUS_CONFIRMED.equals(status)
                || ORDER_STATUS_COMPLETED.equals(status)
                || ORDER_STATUS_CANCELLED.equals(status);
    }

    private boolean isCancellableStatus(String status) {
        return ORDER_STATUS_PENDING_PAYMENT.equals(status) || ORDER_STATUS_CONFIRMED.equals(status);
    }

    private OrderResponse convertToResponse(Order order) {
        long totalItems = orderItemRepository.countByOrderId(order.getId());

        return OrderResponse.builder()
                .id(order.getId())
                .orderId(order.getId())
                .orderCode(order.getOrderCode())
                .receiverName(order.getReceiverName())
                .receiverPhone(order.getReceiverPhone())
                .finalAmount(order.getFinalAmount())
                .paymentMethod(order.getPaymentMethod())
                .paymentStatus(order.getPaymentStatus())
                .orderStatus(order.getOrderStatus())
                .paymentStatusText(resolvePaymentStatusText(order.getPaymentStatus()))
                .orderStatusText(resolveOrderStatusText(order.getOrderStatus()))
                .paymentStatusBadgeClass(resolvePaymentStatusBadgeClass(order.getPaymentStatus()))
                .orderStatusBadgeClass(resolveOrderStatusBadgeClass(order.getOrderStatus()))
                .shippingAddress(order.getShippingAddress())
                .note(order.getNote())
                .totalAmount(order.getTotalAmount())
                .createdAt(order.getCreatedAt())
                .updatedAt(order.getUpdatedAt())
                .totalItems((int) totalItems)
                .cancellable(isCancellableStatus(order.getOrderStatus()))
                .build();
    }

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
                .paymentMethodText(resolvePaymentMethodText(order.getPaymentMethod()))
                .paymentStatus(order.getPaymentStatus())
                .paymentStatusText(resolvePaymentStatusText(order.getPaymentStatus()))
                .paymentStatusBadgeClass(resolvePaymentStatusBadgeClass(order.getPaymentStatus()))
                .orderStatus(order.getOrderStatus())
                .orderStatusText(resolveOrderStatusText(order.getOrderStatus()))
                .orderStatusBadgeClass(resolveOrderStatusBadgeClass(order.getOrderStatus()))
                .cancellable(isCancellableStatus(order.getOrderStatus()))
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

    private String resolvePaymentMethodText(String paymentMethod) {
        return switch (paymentMethod) {
            case PAYMENT_METHOD_COD -> "Thanh toán khi nhận hàng";
            case PAYMENT_METHOD_BANK_QR -> "Chuyển khoản QR ngân hàng";
            default -> paymentMethod;
        };
    }

    private String resolvePaymentStatusText(String paymentStatus) {
        if (PAYMENT_STATUS_PAID.equals(paymentStatus)) {
            return "Đã thanh toán";
        }
        return "Chờ thanh toán";
    }

    private String resolveOrderStatusText(String orderStatus) {
        return switch (orderStatus) {
            case ORDER_STATUS_PENDING_PAYMENT -> "Chờ thanh toán";
            case ORDER_STATUS_CONFIRMED -> "Đã xác nhận";
            case ORDER_STATUS_COMPLETED -> "Hoàn thành";
            case ORDER_STATUS_CANCELLED -> "Đã hủy";
            default -> orderStatus;
        };
    }

    private String resolvePaymentStatusBadgeClass(String paymentStatus) {
        if (PAYMENT_STATUS_PAID.equals(paymentStatus)) {
            return "text-bg-success";
        }
        return "text-bg-warning";
    }

    private String resolveOrderStatusBadgeClass(String orderStatus) {
        return switch (orderStatus) {
            case ORDER_STATUS_PENDING_PAYMENT -> "text-bg-warning";
            case ORDER_STATUS_CONFIRMED -> "text-bg-primary";
            case ORDER_STATUS_COMPLETED -> "text-bg-success";
            case ORDER_STATUS_CANCELLED -> "text-bg-danger";
            default -> "text-bg-secondary";
        };
    }
}
