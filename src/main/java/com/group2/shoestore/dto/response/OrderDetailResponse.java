package com.group2.shoestore.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

/**
 * DTO dùng để trả về thông tin chi tiết đơn hàng
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OrderDetailResponse {

    private Long id;

    private String orderCode;

    // Thông tin khách hàng
    private Long userId;

    private String userName;

    private String receiverName;

    private String receiverPhone;

    private String shippingAddress;

    // Thông tin thanh toán
    private String paymentMethod;

    private String paymentStatus;

    // Thông tin đơn hàng
    private String orderStatus;

    private String note;

    private BigDecimal totalAmount;

    private BigDecimal finalAmount;

    // Danh sách sản phẩm trong đơn
    private List<OrderItemDetail> orderItems;

    // Thời gian
    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;

    /**
     * DTO cho chi tiết sản phẩm trong đơn hàng
     */
    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class OrderItemDetail {

        private Long id;

        private String productName;

        private String color;

        private String size;

        private BigDecimal unitPrice;

        private Integer quantity;

        private BigDecimal subtotal;
    }
}
