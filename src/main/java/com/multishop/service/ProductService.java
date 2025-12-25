// src/main/java/com/multishop/service/ProductService.java
package com.multishop.service;

import com.multishop.model.Product;
import com.multishop.repository.ProductRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ProductService {

    private final ProductRepository productRepo;

    public ProductService(ProductRepository productRepo) {
        this.productRepo = productRepo;
    }

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
            throw new com.multishop.exception.ResourceNotFoundException("Product not found: " + id);
        }
    }
}
