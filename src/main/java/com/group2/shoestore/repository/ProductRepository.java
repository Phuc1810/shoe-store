package com.group2.shoestore.repository;

import com.group2.shoestore.entity.Product;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ProductRepository extends JpaRepository<Product, Long> {

    List<Product> findByStatus(String status);

    List<Product> findByStatusOrderByCreatedAtDesc(String status, Pageable pageable);

    List<Product> findTop8ByStatusOrderByCreatedAtDesc(String status);

    List<Product> findByStatusOrderByIdDesc(String status, Pageable pageable);

    List<Product> findTop5ByStatusAndGenderOrderByIdDesc(String status, String gender);

    List<Product> findByStatusAndGenderOrderByIdDesc(String status, String gender, Pageable pageable);

    List<Product> findByNameContainingIgnoreCase(String name);
}
