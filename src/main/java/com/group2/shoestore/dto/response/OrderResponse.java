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
public class OrderResponse {

    private Long orderId;

    private String orderCode;

    private String receiverName;

    private String receiverPhone;

    private String shippingAddress;

    private String note;

    private BigDecimal totalAmount;

    private BigDecimal finalAmount;

    private String paymentMethod;

    private String paymentStatus;

    private String orderStatus;

    private String paymentStatusText;

    private String orderStatusText;

    private String paymentStatusBadgeClass;

    private String orderStatusBadgeClass;

    private LocalDateTime createdAt;

    private List<OrderItemResponse> items;
}
