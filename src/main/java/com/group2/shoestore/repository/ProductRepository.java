package com.group2.shoestore.repository;

import com.group2.shoestore.entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ProductRepository extends JpaRepository<Product, Long> {

    List<Product> findByStatus(String status);

    List<Product> findByNameContainingIgnoreCase(String name);
}
