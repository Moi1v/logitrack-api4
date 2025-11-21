package com.mcabrera.logitrackapi.services;

import com.mcabrera.logitrackapi.models.OrderItem;
import com.mcabrera.logitrackapi.repositories.OrderItemRepository;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import java.util.List;
import java.util.Optional;

@ApplicationScoped
public class OrderItemService {

    @Inject
    private OrderItemRepository orderItemRepository;

    public List<OrderItem> getAll() {
        return orderItemRepository.getAll();
    }

    public Optional<OrderItem> findById(Long id) {
        return orderItemRepository.findById(id);
    }

    public List<OrderItem> findByOrderId(Long orderId) {
        return orderItemRepository.findByOrderId(orderId);
    }

    public Optional<OrderItem> save(OrderItem orderItem) {
        return orderItemRepository.save(orderItem);
    }

    public void delete(OrderItem orderItem) {
        orderItemRepository.delete(orderItem);
    }

    public void deleteById(Long id) {
        orderItemRepository.deleteById(id);
    }
}