package com.multishop.dto;

public class ProductDto {
    private Long id;
    private String internalSku;
    private String productName;
    private Integer douyinStock;
    private Integer pddStock;
    private Integer totalSales;

    public ProductDto() {}

    public ProductDto(Long id, String internalSku, String productName, Integer douyinStock, Integer pddStock, Integer totalSales) {
        this.id = id;
        this.internalSku = internalSku;
        this.productName = productName;
        this.douyinStock = douyinStock;
        this.pddStock = pddStock;
        this.totalSales = totalSales;
    }

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
