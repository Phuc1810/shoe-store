package com.group2.shoestore.repository;

import com.group2.shoestore.entity.Order;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {

    Optional<Order> findByOrderCode(String orderCode);

    boolean existsByOrderCode(String orderCode);

    // User order history
    List<Order> findByUserIdOrderByCreatedAtDesc(Long userId);

    // Admin order history with pagination
    Page<Order> findByUserIdOrderByCreatedAtDesc(Long userId, Pageable pageable);

    @Query("""
            select o
            from Order o
            where (:orderCode is null or :orderCode = '' or lower(o.orderCode) like lower(concat('%', :orderCode, '%')))
              and (:customerName is null or :customerName = '' or lower(o.receiverName) like lower(concat('%', :customerName, '%')))
              and (:status is null or :status = '' or o.orderStatus = :status)
            order by o.createdAt desc
            """)
    Page<Order> search(@Param("orderCode") String orderCode,
                       @Param("customerName") String customerName,
                       @Param("status") String status,
                       Pageable pageable);

    Page<Order> findByOrderStatusOrderByCreatedAtDesc(String orderStatus, Pageable pageable);

    // Đơn còn có thể xử lý/hủy trong flow hiện tại: PENDING_PAYMENT hoặc CONFIRMED
    @Query("""
            select o
            from Order o
            where o.orderStatus in ('PENDING_PAYMENT', 'CONFIRMED')
            order by o.createdAt desc
            """)
    Page<Order> findPendingOrders(Pageable pageable);

    @Query("select count(o) from Order o")
    long countTotalOrders();

    @Query("select count(o) from Order o where o.orderStatus = :status")
    long countByOrderStatus(@Param("status") String status);

    @Query("select coalesce(sum(o.finalAmount), 0) from Order o where o.orderStatus <> 'CANCELLED'")
    BigDecimal getTotalRevenue();

    @Query("""
            select coalesce(sum(o.finalAmount), 0)
            from Order o
            where date(o.createdAt) = current_date
              and o.orderStatus <> 'CANCELLED'
            """)
    BigDecimal getTodayRevenue();

    @Query("""
            select coalesce(sum(o.finalAmount), 0)
            from Order o
            where year(o.createdAt) = year(current_date)
              and month(o.createdAt) = month(current_date)
              and o.orderStatus <> 'CANCELLED'
            """)
    BigDecimal getMonthRevenue();

    @Query("select count(distinct o.user.id) from Order o")
    long countUniqueCustomers();

    @Query("select o from Order o order by o.createdAt desc")
    List<Order> findRecentOrders(Pageable pageable);
}