package com.group2.shoestore.repository;

import com.group2.shoestore.entity.OrderItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;


@Repository
public interface OrderItemRepository extends JpaRepository<OrderItem, Long> {
    
    // Lấy danh sách chi tiết đơn hàng theo order id
    List<OrderItem> findByOrderId(Long orderId);

    // Đếm số sản phẩm trong một đơn hàng
    long countByOrderId(Long orderId);

    // ===== Dashboard Statistics Methods =====

    // Lấy danh sách sản phẩm bán chạy nhất với doanh thu
    @Query("SELECT oi.productName as name, SUM(oi.quantity) as sold, SUM(oi.subtotal) as revenue " +
           "FROM OrderItem oi GROUP BY oi.productName ORDER BY sold DESC")
    java.util.List<?> findBestSellingProductsStats(org.springframework.data.domain.Pageable pageable);
}
