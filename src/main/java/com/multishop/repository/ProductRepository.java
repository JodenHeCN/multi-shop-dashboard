// src/main/java/com/multishop/repository/ProductRepository.java
package com.multishop.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.multishop.model.Product;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {
}
