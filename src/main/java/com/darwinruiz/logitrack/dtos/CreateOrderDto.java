package com.darwinruiz.logitrack.dtos;

// DTO para crear una orden
public class CreateOrderDto {
    private Long customerId;

    public Long getCustomerId() {
        return customerId;
    }

    public void setCustomerId(Long customerId) {
        this.customerId = customerId;
    }
}

