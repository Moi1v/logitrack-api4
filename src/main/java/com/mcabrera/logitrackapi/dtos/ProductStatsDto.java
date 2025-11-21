package com.mcabrera.logitrackapi.dtos;

import java.math.BigDecimal;

public class ProductStatsDto {
    private Long productId;
    private String name;
    private String category;
    private Long totalQuantitySold;
    private BigDecimal totalRevenue;
    private Long orderCount;

    // Constructor completo
    public ProductStatsDto(Long productId, String name, String category,
                           Long totalQuantitySold, BigDecimal totalRevenue, Long orderCount) {
        this.productId = productId;
        this.name = name;
        this.category = category;
        this.totalQuantitySold = totalQuantitySold;
        this.totalRevenue = totalRevenue;
        this.orderCount = orderCount;
    }

    // Constructor vacío
    public ProductStatsDto() {}

    // Getters y Setters
    public Long getProductId() {
        return productId;
    }

    public void setProductId(Long productId) {
        this.productId = productId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public Long getTotalQuantitySold() {
        return totalQuantitySold;
    }

    public void setTotalQuantitySold(Long totalQuantitySold) {
        this.totalQuantitySold = totalQuantitySold;
    }

    public BigDecimal getTotalRevenue() {
        return totalRevenue;
    }

    public void setTotalRevenue(BigDecimal totalRevenue) {
        this.totalRevenue = totalRevenue;
    }

    public Long getOrderCount() {
        return orderCount;
    }

    public void setOrderCount(Long orderCount) {
        this.orderCount = orderCount;
    }
}