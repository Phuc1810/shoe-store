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

    // Dùng cho admin nếu code admin đang gọi id
    private Long id;

    // Dùng cho user side hiện tại
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

    // Text tiếng Việt cho user UI
    private String paymentStatusText;

    private String orderStatusText;

    private String paymentStatusBadgeClass;

    private String orderStatusBadgeClass;

    private boolean cancellable;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;

    // Dùng cho admin list
    private Integer totalItems;

    // Dùng cho user order detail/success
    private List<OrderItemResponse> items;
}