// src/main/java/com/multishop/model/DailySales.java
package com.multishop.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.Column;
import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Table(name = "daily_sales")
public class DailySales {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "product_id")
    private Long productId;

    @Column(name = "store_id")
    private Integer storeId;

    @Column(name = "date")
    private LocalDate date;

    @Column(name = "sold_qty")
    private Integer soldQty;

    @Column(name = "sold_amount", precision = 12, scale = 2)
    private BigDecimal soldAmount;

    @Column(name = "created_at")
    private java.time.LocalDateTime createdAt;

    // Constructors
    public DailySales() {}

    public DailySales(Long productId, Integer storeId, LocalDate date, Integer soldQty, BigDecimal soldAmount) {
        this.productId = productId;
        this.storeId = storeId;
        this.date = date;
        this.soldQty = soldQty;
        this.soldAmount = soldAmount;
        this.createdAt = java.time.LocalDateTime.now();
    }

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Long getProductId() { return productId; }
    public void setProductId(Long productId) { this.productId = productId; }

    public Integer getStoreId() { return storeId; }
    public void setStoreId(Integer storeId) { this.storeId = storeId; }

    public LocalDate getDate() { return date; }
    public void setDate(LocalDate date) { this.date = date; }

    public Integer getSoldQty() { return soldQty; }
    public void setSoldQty(Integer soldQty) { this.soldQty = soldQty; }

    public BigDecimal getSoldAmount() { return soldAmount; }
    public void setSoldAmount(BigDecimal soldAmount) { this.soldAmount = soldAmount; }

    public java.time.LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(java.time.LocalDateTime createdAt) { this.createdAt = createdAt; }
}