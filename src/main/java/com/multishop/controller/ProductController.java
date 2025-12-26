// src/main/java/com/multishop/controller/ProductController.java
package com.multishop.controller;

import com.multishop.dto.ProductDto;
 
import com.multishop.service.ProductService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/products")
public class ProductController {

    private static final Logger logger = LoggerFactory.getLogger(ProductController.class);

    private final ProductService productService;

    public ProductController(ProductService productService) {
        this.productService = productService;
    }

    @GetMapping
    public List<ProductDto> list() {
        logger.info("GET /api/products - listing products");
        return productService.getAllProducts();
    }

    @PostMapping
    public ProductDto add(@RequestBody ProductDto productDto) {
        logger.info("POST /api/products - adding product: {}", productDto.getInternalSku());
        return productService.addProduct(productDto);
    }

    @PutMapping("/{id}/stock")
    public void updateStock(@PathVariable Long id,
                            @RequestParam Integer douyin,
                            @RequestParam Integer pdd) {
        logger.info("PUT /api/products/{}/stock - douyin={}, pdd={}", id, douyin, pdd);
        productService.updateStock(id, douyin, pdd);
    }
}
