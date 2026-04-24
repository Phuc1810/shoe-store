package com.group2.shoestore.repository;

import com.group2.shoestore.entity.ProductVariant;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ProductVariantRepository extends JpaRepository<ProductVariant, Long> {

    List<ProductVariant> findByProductId(Long productId);

    List<ProductVariant> findByProductIdAndStatus(Long productId, String status);

    Optional<ProductVariant> findFirstByProductIdAndStatusOrderByIdAsc(Long productId, String status);
}
