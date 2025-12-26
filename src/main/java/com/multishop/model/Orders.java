// src/main/java/com/multishop/model/Orders.java
package com.multishop.model;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "orders")
public class Orders {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "platform_order_id")
    private String platformOrderId;

    @Column(name = "store_id")
    private Long storeId;

    @Column(name = "product_id")
    private Long productId;

    @Column(name = "sku")
    private String sku;

    @Column(name = "qty")
    private Integer qty;

    @Column(name = "amount", precision = 12, scale = 2)
    private BigDecimal amount;

    @Column(name = "status")
    private String status;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "raw_payload", columnDefinition = "jsonb")
    private String rawPayload;

    // Constructors
    public Orders() {}

    public Orders(String platformOrderId, Long storeId, Long productId, String sku, Integer qty, BigDecimal amount, String status, String rawPayload) {
        this.platformOrderId = platformOrderId;
        this.storeId = storeId;
        this.productId = productId;
        this.sku = sku;
        this.qty = qty;
        this.amount = amount;
        this.status = status;
        this.rawPayload = rawPayload;
        this.createdAt = LocalDateTime.now();
    }

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getPlatformOrderId() { return platformOrderId; }
    public void setPlatformOrderId(String platformOrderId) { this.platformOrderId = platformOrderId; }

    public Long getStoreId() { return storeId; }
    public void setStoreId(Long storeId) { this.storeId = storeId; }

    public Long getProductId() { return productId; }
    public void setProductId(Long productId) { this.productId = productId; }

    public String getSku() { return sku; }
    public void setSku(String sku) { this.sku = sku; }

    public Integer getQty() { return qty; }
    public void setQty(Integer qty) { this.qty = qty; }

    public BigDecimal getAmount() { return amount; }
    public void setAmount(BigDecimal amount) { this.amount = amount; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public String getRawPayload() { return rawPayload; }
    public void setRawPayload(String rawPayload) { this.rawPayload = rawPayload; }
}