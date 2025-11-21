package com.mcabrera.logitrackapi.dtos;

import java.util.List;

public class CreateOrderDto {
    private Long customerId;
    private List<OrderItemRequestDto> items;

    // Constructors
    public CreateOrderDto() {}

    // Getters and Setters
    public Long getCustomerId() { return customerId; }
    public void setCustomerId(Long customerId) { this.customerId = customerId; }
    public List<OrderItemRequestDto> getItems() { return items; }
    public void setItems(List<OrderItemRequestDto> items) { this.items = items; }
}