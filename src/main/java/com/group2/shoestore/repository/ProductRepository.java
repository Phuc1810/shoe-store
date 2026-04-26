package com.group2.shoestore.repository;

import com.group2.shoestore.entity.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ProductRepository extends JpaRepository<Product,Long> {

    Optional<Product> findBySlug(String slug);
    @Query("""
        SELECT p FROM Product p
        WHERE (:name IS NULL OR p.name LIKE %:name%)
            AND (:categoryId IS NULL OR p.category.id = :categoryId)
            AND (:brandId IS NULL OR p.brand.id = :brandId)
            AND (:status IS NULL OR p.status = :status)
        ORDER BY p.createdAt DESC
    """)
    Page<Product> search(
            @Param("name") String name,
            @Param("categoryId") Long categoryId,
            @Param("brandId") Long brandId,
            @Param("status") String status,
            Pageable pageable
    );
}
