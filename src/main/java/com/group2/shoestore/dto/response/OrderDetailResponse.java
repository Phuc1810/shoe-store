package com.group2.shoestore.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OrderDetailResponse {

    private Long id;

    private String orderCode;

    private Long userId;

    private String userName;

    private String receiverName;

    private String receiverPhone;

    private String shippingAddress;

    private String paymentMethod;

    private String paymentMethodText;

    private String paymentStatus;

    private String paymentStatusText;

    private String paymentStatusBadgeClass;

    private String orderStatus;

    private String orderStatusText;

    private String orderStatusBadgeClass;

    private boolean cancellable;

    private String note;

    private BigDecimal totalAmount;

    private BigDecimal finalAmount;

    private List<OrderItemDetail> orderItems;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;

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
