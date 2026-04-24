package com.group2.shoestore.service.user;

import com.group2.shoestore.dto.response.ProductCardResponse;
import com.group2.shoestore.dto.response.ProductDetailResponse;
import com.group2.shoestore.dto.response.ProductVariantResponse;
import com.group2.shoestore.entity.Product;
import com.group2.shoestore.entity.ProductVariant;
import com.group2.shoestore.exception.ResourceNotFoundException;
import com.group2.shoestore.repository.ProductRepository;
import com.group2.shoestore.repository.ProductVariantRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.Comparator;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ProductService {

    private static final String ACTIVE_STATUS = "ACTIVE";
    private static final String PLACEHOLDER_IMAGE_URL = "/images/logo.png";

    private final ProductRepository productRepository;
    private final ProductVariantRepository productVariantRepository;

    @Transactional(readOnly = true)
    public List<ProductCardResponse> getActiveProductCards() {
        return productRepository.findByStatus(ACTIVE_STATUS)
                .stream()
                .map(this::toProductCardResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public ProductDetailResponse getActiveProductDetail(Long productId) {
        Product product = productRepository.findById(productId)
                .filter(item -> ACTIVE_STATUS.equals(item.getStatus()))
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy sản phẩm"));

        List<ProductVariant> activeVariants = productVariantRepository
                .findByProductIdAndStatus(productId, ACTIVE_STATUS)
                .stream()
                .sorted(Comparator.comparing(ProductVariant::getId))
                .toList();

        List<ProductVariantResponse> variants = activeVariants
                .stream()
                .map(this::toProductVariantResponse)
                .toList();

        ProductVariant firstActiveVariant = activeVariants.stream()
                .findFirst()
                .orElse(null);

        ProductVariant firstActiveVariantWithImage = activeVariants.stream()
                .filter(variant -> variant.getImageUrl() != null && !variant.getImageUrl().isBlank())
                .findFirst()
                .orElse(null);

        return ProductDetailResponse.builder()
                .productId(product.getId())
                .productName(product.getName())
                .brandName(product.getBrand().getName())
                .categoryName(product.getCategory().getName())
                .gender(product.getGender())
                .shortDescription(product.getShortDescription())
                .description(product.getDescription())
                .status(product.getStatus())
                .mainImageUrl(resolveImageUrl(firstActiveVariantWithImage))
                .displayPrice(resolvePrice(product, firstActiveVariant))
                .variants(variants)
                .build();
    }

    private ProductCardResponse toProductCardResponse(Product product) {
        ProductVariant firstActiveVariant = productVariantRepository
                .findFirstByProductIdAndStatusOrderByIdAsc(product.getId(), ACTIVE_STATUS)
                .orElse(null);

        return ProductCardResponse.builder()
                .productId(product.getId())
                .productName(product.getName())
                .brandName(product.getBrand().getName())
                .categoryName(product.getCategory().getName())
                .gender(product.getGender())
                .price(resolvePrice(product, firstActiveVariant))
                .imageUrl(resolveImageUrl(firstActiveVariant))
                .build();
    }

    private BigDecimal resolvePrice(Product product, ProductVariant variant) {
        if (variant != null && variant.getPrice() != null) {
            return variant.getPrice();
        }
        return product.getBasePrice();
    }

    private String resolveImageUrl(ProductVariant variant) {
        if (variant == null || variant.getImageUrl() == null || variant.getImageUrl().isBlank()) {
            return PLACEHOLDER_IMAGE_URL;
        }
        return variant.getImageUrl();
    }

    private ProductVariantResponse toProductVariantResponse(ProductVariant variant) {
        return ProductVariantResponse.builder()
                .variantId(variant.getId())
                .sku(variant.getSku())
                .color(variant.getColor())
                .size(variant.getSize())
                .price(variant.getPrice())
                .stockQuantity(variant.getStockQuantity())
                .imageUrl(resolveImageUrl(variant))
                .status(variant.getStatus())
                .build();
    }
}
