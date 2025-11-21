package com.darwinruiz.logitrack.dtos;

// DTO para actualizar estado de orden
public class UpdateOrderStatusDto {
    private String status;

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
