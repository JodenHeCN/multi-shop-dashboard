// src/main/java/com/multishop/service/ProductService.java
package com.multishop.service;

import com.multishop.dto.ProductDto;
import com.multishop.mapper.ProductMapper;
import com.multishop.model.Product;
import com.multishop.repository.ProductRepository;
import org.springframework.stereotype.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Optional;

@Service
public class ProductService {

    private static final Logger logger = LoggerFactory.getLogger(ProductService.class);

    private final ProductRepository productRepo;

    public ProductService(ProductRepository productRepo) {
        this.productRepo = productRepo;
    }

    /** Return DTO list (service performs mapping). */
    public List<ProductDto> getAllProducts() {
        logger.debug("Fetching all products (dto)");
        List<Product> list = productRepo.findAll();
        return list.stream().map(ProductMapper::toDto).toList();
    }

    /** Accept DTO, save entity, return DTO. */
    public ProductDto addProduct(ProductDto productDto) {
        Product entity = ProductMapper.toEntity(productDto);
        logger.debug("Saving product: {}", entity.getInternalSku());
        Product saved = productRepo.save(entity);
        return ProductMapper.toDto(saved);
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
