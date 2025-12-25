// src/main/java/com/example/multishop/controller/ProductController.java
package com.example.multishop.controller;

import com.example.multishop.model.Product;
import com.example.multishop.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/products")
public class ProductController {

    @Autowired
    private ProductService productService;

    @GetMapping
    public List<Product> list() {
        return productService.getAllProducts();
    }

    @PostMapping
    public Product add(@RequestBody Product product) {
        return productService.addProduct(product);
    }

    @PutMapping("/{id}/stock")
    public void updateStock(@PathVariable Long id,
                            @RequestParam Integer douyin,
                            @RequestParam Integer pdd) {
        productService.updateStock(id, douyin, pdd);
    }
}