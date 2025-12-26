package com.multishop.mapper;

import com.multishop.dto.ProductDto;
import com.multishop.model.Product;

public class ProductMapper {

    public static Product toEntity(ProductDto dto) {
        if (dto == null) return null;
        Product p = new Product();
        p.setId(dto.getId());
        p.setInternalSku(dto.getInternalSku());
        p.setProductName(dto.getProductName());
        p.setDouyinStock(dto.getDouyinStock() != null ? dto.getDouyinStock() : 0);
        p.setPddStock(dto.getPddStock() != null ? dto.getPddStock() : 0);
        p.setTotalSales(dto.getTotalSales() != null ? dto.getTotalSales() : 0);
        return p;
    }

    public static ProductDto toDto(Product p) {
        if (p == null) return null;
        return new ProductDto(p.getId(), p.getInternalSku(), p.getProductName(), p.getDouyinStock(), p.getPddStock(), p.getTotalSales());
    }
}
