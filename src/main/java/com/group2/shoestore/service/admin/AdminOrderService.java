package com.group2.shoestore.service.admin;

import com.group2.shoestore.dto.response.OrderDetailResponse;
import com.group2.shoestore.dto.response.OrderResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

/**
 * Service interface cho quản lý đơn hàng
 */
public interface AdminOrderService {

    /**
     * Tìm kiếm đơn hàng với bộ lọc và phân trang
     */
    Page<OrderResponse> search(String orderCode, String customerName, String status, Pageable pageable);

    /**
     * Lấy thông tin chi tiết đơn hàng theo ID
     */
    OrderDetailResponse getOrderById(Long id);

    /**
     * Cập nhật trạng thái đơn hàng
     */
    OrderDetailResponse updateOrderStatus(Long id, String newStatus);

    /**
     * Hủy đơn hàng
     */
    void cancelOrder(Long id);

    /**
     * Lấy danh sách đơn hàng theo trạng thái
     */
    Page<OrderResponse> getOrdersByStatus(String status, Pageable pageable);
}
