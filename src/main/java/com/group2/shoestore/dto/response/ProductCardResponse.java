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
public class ProductCardResponse {

    private Long productId;

    private String productName;

    private String brandName;

    private String categoryName;

    private String gender;

    private BigDecimal price;

    private String imageUrl;

    private String status;

    private Long soldQuantity;
}
