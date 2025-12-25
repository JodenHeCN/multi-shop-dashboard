// src/main/java/com/multishop/model/Product.java
package com.multishop.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "products")
public class Product {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String internalSku;     // 内部编码，如 PROD-001
    private String productName;
    private Integer douyinStock = 0;
    private Integer pddStock = 0;
    private Integer totalSales = 0; // 可按天拆分，这里简化

    // Constructors
    public Product() {}
    public Product(String internalSku, String productName) {
        this.internalSku = internalSku;
        this.productName = productName;
    }

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getInternalSku() { return internalSku; }
    public void setInternalSku(String internalSku) { this.internalSku = internalSku; }

    public String getProductName() { return productName; }
    public void setProductName(String productName) { this.productName = productName; }

    public Integer getDouyinStock() { return douyinStock; }
    public void setDouyinStock(Integer douyinStock) { this.douyinStock = douyinStock; }

    public Integer getPddStock() { return pddStock; }
    public void setPddStock(Integer pddStock) { this.pddStock = pddStock; }

    public Integer getTotalSales() { return totalSales; }
    public void setTotalSales(Integer totalSales) { this.totalSales = totalSales; }
}
