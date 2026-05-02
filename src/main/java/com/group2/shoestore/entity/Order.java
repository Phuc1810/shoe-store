package com.group2.shoestore.entity;


import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "orders")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Order {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "order_code", nullable = false, unique = true, length = 30)
    private String orderCode;

    // Liên kết với User
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(name = "receiver_name", nullable = false, length = 100)
    private String receiverName;

    @Column(name = "receiver_phone", nullable = false, length = 20)
    private String receiverPhone;

    @Column(name = "shipping_address", nullable = false, length = 255)
    private String shippingAddress;

    @Column(name = "note", length = 255)
    private String note;

    @Column(name = "total_amount", nullable = false, precision = 12, scale = 2)
    private BigDecimal totalAmount;

    @Column(name = "final_amount", nullable = false, precision = 12, scale = 2)
    private BigDecimal finalAmount;

    @Column(name = "payment_method", nullable = false, length = 30)
    private String paymentMethod; // CASH, CREDIT_CARD, BANK_TRANSFER

    @Column(name = "payment_status", length = 20)
    private String paymentStatus = "PENDING"; // PENDING, PAID, FAILED, REFUNDED

    @Column(name = "order_status", length = 30)
    private String orderStatus = "CONFIRMED"; // CONFIRMED, SHIPPING, DELIVERED, CANCELLED

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    // Liên kết với OrderItem (một đơn hàng có nhiều chi tiết đơn hàng)
    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<OrderItem> orderItems = new ArrayList<>();


    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }


    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
}