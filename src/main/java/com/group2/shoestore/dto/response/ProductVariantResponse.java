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
public class ProductVariantResponse {

    private Long variantId;

    private String sku;

    private String color;

    private String size;

    private BigDecimal price;

    private Integer stockQuantity;

    private String imageUrl;

    private String status;
}
