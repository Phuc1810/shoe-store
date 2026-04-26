package com.group2.shoestore.service.admin;

import com.group2.shoestore.dto.request.ProductRequest;
import com.group2.shoestore.dto.response.ProductListItemResponse;
import com.group2.shoestore.dto.response.ProductResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;


public interface AdminProductService {


    Page<ProductListItemResponse> search(String name, Long categoryId, Long brandId, String status, Pageable pageable);


    ProductResponse getProductById(Long id);


    ProductResponse createProduct(ProductRequest request);


    ProductResponse updateProduct(Long id, ProductRequest request);


    void deleteProduct(Long id);


    boolean isSlugExists(String slug);

    boolean isSlugExistsExcludeId(String slug, Long id);
}
