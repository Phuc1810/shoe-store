package com.group2.shoestore.dto.response;


import lombok.*;

import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProductListItemResponse {
    private Long id;
    private String name;
    private String categoryName;
    private String brandName;
    private BigDecimal basePrice;
    private String status;
}
