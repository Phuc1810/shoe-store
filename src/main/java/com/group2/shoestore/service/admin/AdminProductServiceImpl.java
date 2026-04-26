package com.group2.shoestore.service.admin;

import com.group2.shoestore.dto.request.ProductRequest;
import com.group2.shoestore.dto.response.ProductListItemResponse;
import com.group2.shoestore.dto.response.ProductResponse;
import com.group2.shoestore.entity.Brand;
import com.group2.shoestore.entity.Category;
import com.group2.shoestore.entity.Product;
import com.group2.shoestore.exception.ResourceNotFoundException;
import com.group2.shoestore.repository.BrandRepository;
import com.group2.shoestore.repository.CategoryRepository;
import com.group2.shoestore.repository.ProductRepository;
import com.group2.shoestore.util.SlugUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Slf4j
@RequiredArgsConstructor
@Transactional
public class AdminProductServiceImpl implements AdminProductService {

    private final ProductRepository productRepository;
    private final CategoryRepository categoryRepository;
    private final BrandRepository brandRepository;


    @Override
    @Transactional(readOnly = true)
    public Page<ProductListItemResponse> search(String name, Long categoryId, Long brandId, String status, Pageable pageable) {
        log.info("Searching products: name={}, categoryId={}, brandId={}, status={}", name, categoryId, brandId, status);

        Page<Product> products = productRepository.search(name, categoryId, brandId, status, pageable);

        return products.map(this::convertToListItemResponse);
    }


    @Override
    @Transactional(readOnly = true)
    public ProductResponse getProductById(Long id) {
        log.info("Getting product by id: {}", id);

        Product product = productRepository.findById(id)
                .orElseThrow(() -> {
                    log.error("Product not found with id: {}", id);
                    return new ResourceNotFoundException("Sản phẩm không tồn tại");
                });

        return convertToResponse(product);
    }


    @Override
    public ProductResponse createProduct(ProductRequest request) {
        log.info("Creating new product: {}", request.getName());

        // Kiểm tra category tồn tại
        Category category = categoryRepository.findById(request.getCategoryId())
                .orElseThrow(() -> {
                    log.error("Category not found with id: {}", request.getCategoryId());
                    return new ResourceNotFoundException("Danh mục không tồn tại");
                });

        // Kiểm tra brand nếu có
        Brand brand = null;
        if (request.getBrandId() != null && request.getBrandId() > 0) {
            brand = brandRepository.findById(request.getBrandId())
                    .orElseThrow(() -> {
                        log.error("Brand not found with id: {}", request.getBrandId());
                        return new ResourceNotFoundException("Thương hiệu không tồn tại");
                    });
        }

        // Tạo slug từ tên sản phẩm
        String slug = SlugUtil.toSlug(request.getName());

        // Tạo entity Product
        Product product = new Product();
        product.setName(request.getName());
        product.setSlug(slug);
        product.setCategory(category);
        product.setBrand(brand);
        product.setGender(request.getGender());
        product.setShortDescription(request.getShortDescription());
        product.setDescription(request.getDescription());
        product.setBasePrice(request.getBasePrice());
        product.setStatus(request.getStatus());

        Product savedProduct = productRepository.save(product);
        log.info("Product created successfully with id: {}", savedProduct.getId());

        return convertToResponse(savedProduct);
    }

    /**
     * Cập nhật sản phẩm
     */
    @Override
    public ProductResponse updateProduct(Long id, ProductRequest request) {
        log.info("Updating product with id: {}", id);

        Product product = productRepository.findById(id)
                .orElseThrow(() -> {
                    log.error("Product not found with id: {}", id);
                    return new ResourceNotFoundException("Sản phẩm không tồn tại");
                });

        // Kiểm tra category
        Category category = categoryRepository.findById(request.getCategoryId())
                .orElseThrow(() -> {
                    log.error("Category not found with id: {}", request.getCategoryId());
                    return new ResourceNotFoundException("Danh mục không tồn tại");
                });

        // Kiểm tra brand nếu có
        Brand brand = null;
        if (request.getBrandId() != null && request.getBrandId() > 0) {
            brand = brandRepository.findById(request.getBrandId())
                    .orElseThrow(() -> {
                        log.error("Brand not found with id: {}", request.getBrandId());
                        return new ResourceNotFoundException("Thương hiệu không tồn tại");
                    });
        }

        // Cập nhật dữ liệu
        product.setName(request.getName());
        product.setSlug(SlugUtil.toSlug(request.getName()));
        product.setCategory(category);
        product.setBrand(brand);
        product.setGender(request.getGender());
        product.setShortDescription(request.getShortDescription());
        product.setDescription(request.getDescription());
        product.setBasePrice(request.getBasePrice());
        product.setStatus(request.getStatus());

        Product updatedProduct = productRepository.save(product);
        log.info("Product updated successfully with id: {}", updatedProduct.getId());

        return convertToResponse(updatedProduct);
    }


    @Override
    public void deleteProduct(Long id) {
        log.info("Deleting product with id: {}", id);

        Product product = productRepository.findById(id)
                .orElseThrow(() -> {
                    log.error("Product not found with id: {}", id);
                    return new ResourceNotFoundException("Sản phẩm không tồn tại");
                });

        productRepository.delete(product);
        log.info("Product deleted successfully with id: {}", id);
    }


    @Override
    @Transactional(readOnly = true)
    public boolean isSlugExists(String slug) {
        return productRepository.findBySlug(slug).isPresent();
    }


    @Override
    @Transactional(readOnly = true)
    public boolean isSlugExistsExcludeId(String slug, Long id) {
        return productRepository.findBySlug(slug)
                .map(product -> !product.getId().equals(id))
                .orElse(false);
    }


    private ProductResponse convertToResponse(Product product) {
        return ProductResponse.builder()
                .id(product.getId())
                .name(product.getName())
                .slug(product.getSlug())
                .categoryId(product.getCategory().getId())
                .categoryName(product.getCategory().getName())
                .brandId(product.getBrand() != null ? product.getBrand().getId() : null)
                .brandName(product.getBrand() != null ? product.getBrand().getName() : null)
                .gender(product.getGender())
                .shortDescription(product.getShortDescription())
                .description(product.getDescription())
                .basePrice(product.getBasePrice())
                .status(product.getStatus())
                .createdAt(product.getCreatedAt())
                .updatedAt(product.getUpdatedAt())
                .build();
    }


    private ProductListItemResponse convertToListItemResponse(Product product) {
        return ProductListItemResponse.builder()
                .id(product.getId())
                .name(product.getName())
                .categoryName(product.getCategory().getName())
                .brandName(product.getBrand() != null ? product.getBrand().getName() : "N/A")
                .basePrice(product.getBasePrice())
                .status(product.getStatus())
                .build();
    }
}
