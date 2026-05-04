package com.group2.shoestore.service.admin;

import com.group2.shoestore.dto.response.OrderDetailResponse;
import com.group2.shoestore.dto.response.OrderResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;


public interface AdminOrderService {

    Page<OrderResponse> search(String orderCode, String customerName, String status, Pageable pageable);


    OrderDetailResponse getOrderById(Long id);


    OrderDetailResponse updateOrderStatus(Long id, String newStatus);


    void cancelOrder(Long id);


    Page<OrderResponse> getOrdersByStatus(String status, Pageable pageable);
}
