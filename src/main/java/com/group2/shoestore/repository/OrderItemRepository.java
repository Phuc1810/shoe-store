package com.group2.shoestore.repository;

import com.group2.shoestore.entity.OrderItem;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface OrderItemRepository extends JpaRepository<OrderItem, Long> {

    List<OrderItem> findByOrderId(Long orderId);

    @Query("""
            select pv.product.id, sum(oi.quantity)
            from OrderItem oi
            join oi.productVariant pv
            join oi.order ord
            where ord.orderStatus <> 'CANCELLED'
            group by pv.product.id
            order by sum(oi.quantity) desc
            """)
    List<Object[]> findHotProductSales(Pageable pageable);
}
