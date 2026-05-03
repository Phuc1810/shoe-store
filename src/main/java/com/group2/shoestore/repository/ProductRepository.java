package com.group2.shoestore.repository;

import com.group2.shoestore.entity.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface ProductRepository extends JpaRepository<Product, Long> {

    List<Product> findByStatus(String status);

    List<Product> findByStatusOrderByCreatedAtDesc(String status, Pageable pageable);

    List<Product> findTop8ByStatusOrderByCreatedAtDesc(String status);

    List<Product> findByStatusOrderByIdDesc(String status, Pageable pageable);

    List<Product> findTop5ByStatusAndGenderOrderByIdDesc(String status, String gender);

    List<Product> findByStatusAndGenderOrderByIdDesc(String status, String gender, Pageable pageable);

    List<Product> findByNameContainingIgnoreCase(String name);

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
