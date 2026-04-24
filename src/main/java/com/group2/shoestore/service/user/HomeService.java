package com.group2.shoestore.service.user;

import com.group2.shoestore.dto.response.HomeViewModel;
import com.group2.shoestore.dto.response.ProductCardResponse;
import com.group2.shoestore.entity.Product;
import com.group2.shoestore.entity.ProductVariant;
import com.group2.shoestore.repository.OrderItemRepository;
import com.group2.shoestore.repository.ProductRepository;
import com.group2.shoestore.repository.ProductVariantRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class HomeService {

    private static final String ACTIVE_STATUS = "ACTIVE";
    private static final String PLACEHOLDER_IMAGE_URL = "/images/logo.png";

    private static final List<String> BANNER_IMAGES = List.of(
            "https://images.unsplash.com/photo-1549298916-b41d501d3772?auto=format&fit=crop&w=1600&q=80",
            "https://images.unsplash.com/photo-1543508282-6319a3e2621f?auto=format&fit=crop&w=1600&q=80",
            "https://images.unsplash.com/photo-1608231387042-66d1773070a5?auto=format&fit=crop&w=1600&q=80",
            "https://images.unsplash.com/photo-1600185365483-26d7a4cc7519?auto=format&fit=crop&w=1600&q=80",
            "https://images.unsplash.com/photo-1515955656352-a1fa3ffcd111?auto=format&fit=crop&w=1600&q=80"
    );

    private final ProductRepository productRepository;
    private final ProductVariantRepository productVariantRepository;
    private final OrderItemRepository orderItemRepository;

    @Transactional(readOnly = true)
    public HomeViewModel getHomeData() {
        return HomeViewModel.builder()
                .newestProducts(getNewestProducts(8))
                .hotProducts(getHotProducts(8))
                .shoeDropdownProducts(getNewestProducts(5))
                .menProducts(getProductsByGender("MEN", 5))
                .womenProducts(getProductsByGender("WOMEN", 5))
                .kidsProducts(getProductsByGender("KIDS", 5))
                .unisexProducts(getProductsByGender("UNISEX", 5))
                .bannerImages(BANNER_IMAGES)
                .build();
    }

    @Transactional(readOnly = true)
    public List<ProductCardResponse> getNewestProducts(int limit) {
        return productRepository.findByStatusOrderByCreatedAtDesc(ACTIVE_STATUS, PageRequest.of(0, limit))
                .stream()
                .map(product -> toProductCardResponse(product, 0L))
                .toList();
    }

    @Transactional(readOnly = true)
    public List<ProductCardResponse> getProductsByGender(String gender, int limit) {
        return productRepository.findByStatusAndGenderOrderByIdDesc(ACTIVE_STATUS, gender, PageRequest.of(0, limit))
                .stream()
                .map(product -> toProductCardResponse(product, 0L))
                .toList();
    }

    @Transactional(readOnly = true)
    public List<ProductCardResponse> getHotProducts(int limit) {
        List<Object[]> hotRows = orderItemRepository.findHotProductSales(PageRequest.of(0, limit));
        if (hotRows.isEmpty()) {
            return getFallbackHotProducts(limit);
        }

        Map<Long, Long> soldQuantityByProductId = new LinkedHashMap<>();
        for (Object[] row : hotRows) {
            Long productId = (Long) row[0];
            Number soldQuantity = (Number) row[1];
            soldQuantityByProductId.put(productId, soldQuantity == null ? 0L : soldQuantity.longValue());
        }

        Map<Long, Product> productById = productRepository.findAllById(soldQuantityByProductId.keySet())
                .stream()
                .filter(product -> ACTIVE_STATUS.equals(product.getStatus()))
                .collect(Collectors.toMap(Product::getId, Function.identity()));

        List<ProductCardResponse> hotProducts = soldQuantityByProductId.entrySet()
                .stream()
                .map(entry -> {
                    Product product = productById.get(entry.getKey());
                    if (product == null) {
                        return null;
                    }
                    return toProductCardResponse(product, entry.getValue());
                })
                .filter(product -> product != null)
                .toList();

        if (hotProducts.isEmpty()) {
            return getFallbackHotProducts(limit);
        }
        return hotProducts;
    }

    private List<ProductCardResponse> getFallbackHotProducts(int limit) {
        return productRepository.findByStatusOrderByIdDesc(ACTIVE_STATUS, PageRequest.of(0, limit))
                .stream()
                .map(product -> toProductCardResponse(product, 0L))
                .toList();
    }

    private ProductCardResponse toProductCardResponse(Product product, Long soldQuantity) {
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
                .status(product.getStatus())
                .soldQuantity(soldQuantity)
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
}
