package com.group2.shoestore.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "product_variants")
public class ProductVariant {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id", nullable = false)
    private Product product;

    @Column(name = "sku", unique = true, length = 100)
    private String sku;

    @Column(name = "color", length = 100)
    private String color;

    @Column(name = "size", length = 20)
    private String size;

    @Column(name = "price", nullable = false, precision = 12, scale = 2)
    private BigDecimal price;

    @Column(name = "stock_quantity")
    private Integer stockQuantity;

    @Column(name = "image_url", length = 1000)
    private String imageUrl;

    @Column(name = "status", length = 20)
    private String status;

    @Builder.Default
    @OneToMany(mappedBy = "productVariant", fetch = FetchType.LAZY)
    private List<CartItem> cartItems = new ArrayList<>();

    @Builder.Default
    @OneToMany(mappedBy = "productVariant", fetch = FetchType.LAZY)
    private List<OrderItem> orderItems = new ArrayList<>();
}
