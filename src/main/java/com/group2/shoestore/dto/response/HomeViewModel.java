package com.group2.shoestore.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class HomeViewModel {

    private List<ProductCardResponse> newestProducts;

    private List<ProductCardResponse> hotProducts;

    private List<ProductCardResponse> menProducts;

    private List<ProductCardResponse> womenProducts;

    private List<ProductCardResponse> kidsProducts;

    private List<ProductCardResponse> unisexProducts;

    private List<ProductCardResponse> shoeDropdownProducts;

    private List<String> bannerImages;
}
