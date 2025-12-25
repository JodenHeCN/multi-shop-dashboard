// src/main/java/com/example/multishop/service/ProductService.java
package com.example.multishop.service;

import com.example.multishop.model.Product;
import com.example.multishop.repository.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ProductService {

    @Autowired
    private ProductRepository productRepo;

    public List<Product> getAllProducts() {
        return productRepo.findAll();
    }

    public Product addProduct(Product product) {
        return productRepo.save(product);
    }

    public void updateStock(Long id, Integer douyinStock, Integer pddStock) {
        Optional<Product> existing = productRepo.findById(id);
        if (existing.isPresent()) {
            Product p = existing.get();
            p.setDouyinStock(douyinStock);
            p.setPddStock(pddStock);
            productRepo.save(p);
        } else {
            throw new RuntimeException("Product not found: " + id);
        }
    }
}