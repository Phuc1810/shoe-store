package com.group2.shoestore.repository;

import com.group2.shoestore.entity.Order;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;


@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {
    
    // Tìm đơn hàng theo mã đơn hàng
    Optional<Order> findByOrderCode(String orderCode);

    // Kiểm tra xem mã đơn hàng đã tồn tại chưa
    boolean existsByOrderCode(String orderCode);

    // Tìm kiếm đơn hàng theo mã đơn hàng hoặc tên khách hàng, với trạng thái
//    @Query("SELECT o FROM Order o WHERE " +
//           "(:orderCode IS NULL OR LOWER(o.orderCode) LIKE LOWER(CONCAT('%', :orderCode, '%'))) " +
//           "AND (:customerName IS NULL OR LOWER(o.receiverName) LIKE LOWER(CONCAT('%', :customerName, '%'))) " +
//           "AND (:status IS NULL OR o.orderStatus = :status) " +
//           "ORDER BY o.createdAt DESC")
//    Page<Order> search(@Param("orderCode") String orderCode,
//                       @Param("customerName") String customerName,
//                       @Param("status") String status,
//                       Pageable pageable);
    @Query("SELECT o FROM Order o WHERE " +
            "(:orderCode IS NULL OR :orderCode = '' OR LOWER(o.orderCode) LIKE LOWER(CONCAT('%', :orderCode, '%'))) " +
            "AND (:customerName IS NULL OR :customerName = '' OR LOWER(o.receiverName) LIKE LOWER(CONCAT('%', :customerName, '%'))) " +
            "AND (:status IS NULL OR :status = '' OR o.orderStatus = :status) " +
            "ORDER BY o.createdAt DESC")
    Page<Order> search(@Param("orderCode") String orderCode,
                       @Param("customerName") String customerName,
                       @Param("status") String status,
                       Pageable pageable);

    // Lấy danh sách đơn hàng theo trạng thái
    Page<Order> findByOrderStatusOrderByCreatedAtDesc(String orderStatus, Pageable pageable);

    // Lấy danh sách đơn hàng của một user
    Page<Order> findByUserIdOrderByCreatedAtDesc(Long userId, Pageable pageable);

    // Lấy danh sách đơn hàng chưa được giao (dùng cho hủy đơn)
    @Query("SELECT o FROM Order o WHERE o.orderStatus != 'DELIVERED' AND o.orderStatus != 'CANCELLED' " +
           "ORDER BY o.createdAt DESC")
    Page<Order> findPendingOrders(Pageable pageable);

    // ===== Dashboard Statistics Methods =====

    // Đếm tổng số đơn hàng
    @Query("SELECT COUNT(o) FROM Order o")
    long countTotalOrders();

    // Đếm số đơn hàng theo trạng thái
    @Query("SELECT COUNT(o) FROM Order o WHERE o.orderStatus = :status")
    long countByOrderStatus(@Param("status") String status);

    // Lấy tổng doanh thu (tổng finalAmount)
    @Query("SELECT COALESCE(SUM(o.finalAmount), 0) FROM Order o WHERE o.orderStatus != 'CANCELLED'")
    java.math.BigDecimal getTotalRevenue();

    // Lấy doanh thu hôm nay
    @Query("SELECT COALESCE(SUM(o.finalAmount), 0) FROM Order o " +
           "WHERE DATE(o.createdAt) = CURDATE() AND o.orderStatus != 'CANCELLED'")
    java.math.BigDecimal getTodayRevenue();

    // Lấy doanh thu của tháng hiện tại
    @Query("SELECT COALESCE(SUM(o.finalAmount), 0) FROM Order o " +
           "WHERE YEAR(o.createdAt) = YEAR(CURDATE()) " +
           "AND MONTH(o.createdAt) = MONTH(CURDATE()) " +
           "AND o.orderStatus != 'CANCELLED'")
    java.math.BigDecimal getMonthRevenue();

    // Lấy số đơn hàng unique customers
    @Query("SELECT COUNT(DISTINCT o.user.id) FROM Order o")
    long countUniqueCustomers();

    // Lấy danh sách đơn hàng gần đây nhất (top N)
    @Query("SELECT o FROM Order o ORDER BY o.createdAt DESC")
    List<Order> findRecentOrders(Pageable pageable);
}
