package com.group2.shoestore.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CartItemResponse {

    private Long cartItemId;

    private Long productVariantId;

    private String productName;

    private String imageUrl;

    private String color;

    private String size;

    private BigDecimal unitPrice;

    private Integer quantity;

    private BigDecimal subtotal;
}
