// src/main/java/com/multishop/controller/ProductController.java
package com.multishop.controller;

import com.multishop.model.Product;
import com.multishop.service.ProductService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/products")
public class ProductController {

    private final ProductService productService;

    public ProductController(ProductService productService) {
        this.productService = productService;
    }

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
