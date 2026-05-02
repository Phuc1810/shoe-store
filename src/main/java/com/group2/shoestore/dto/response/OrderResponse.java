package com.group2.shoestore.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * DTO dùng để trả về thông tin đơn hàng trong danh sách
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OrderResponse {

    private Long id;

    private String orderCode;

    private String receiverName;

    private String receiverPhone;

    private BigDecimal finalAmount;

    private String paymentMethod;

    private String paymentStatus;

    private String orderStatus;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;

    private Integer totalItems;
}
