package com.mcabrera.logitrackapi.dtos;

public class OrderItemRequestDto {
    private Long productId;
    private Integer quantity;

    // Constructors
    public OrderItemRequestDto() {}

    // Getters and Setters
    public Long getProductId() { return productId; }
    public void setProductId(Long productId) { this.productId = productId; }
    public Integer getQuantity() { return quantity; }
    public void setQuantity(Integer quantity) { this.quantity = quantity; }
}