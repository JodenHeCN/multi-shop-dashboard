// src/main/java/com/example/multishop/repository/ProductRepository.java
package com.example.multishop.repository;

import com.example.multishop.model.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {
}