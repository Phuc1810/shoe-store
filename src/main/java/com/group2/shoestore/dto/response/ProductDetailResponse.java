package com.group2.shoestore.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProductDetailResponse {

    private Long productId;

    private String productName;

    private String brandName;

    private String categoryName;

    private String gender;

    private String shortDescription;

    private String description;

    private String status;

    private String mainImageUrl;

    private BigDecimal displayPrice;

    private List<ProductVariantResponse> variants;
}
