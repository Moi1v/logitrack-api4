package com.darwinruiz.logitrack.repositories;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.persistence.TypedQuery;

import java.util.List;

@ApplicationScoped
public class OrderRepository extends BaseRepository<Order, Long> {

    @Override
    protected Class<Order> entity() {
        return Order.class;
    }

    public List<Order> findByCustomerId(Long customerId) {
        TypedQuery<Order> query = entityManager.createQuery(
                "SELECT o FROM Order o WHERE o.customer.customerId = :customerId ORDER BY o.orderDate DESC",
                Order.class
        );
        query.setParameter("customerId", customerId);
        return query.getResultList();
    }

    public List<Order> findByStatus(OrderStatus status) {
        TypedQuery<Order> query = entityManager.createQuery(
                "SELECT o FROM Order o WHERE o.status = :status",
                Order.class
        );
        query.setParameter("status", status);
        return query.getResultList();
    }

    public List<Order> findByDateRange(LocalDate startDate, LocalDate endDate) {
        TypedQuery<Order> query = entityManager.createQuery(
                "SELECT o FROM Order o WHERE o.orderDate BETWEEN :start AND :end ORDER BY o.orderDate DESC",
                Order.class
        );
        query.setParameter("start", startDate);
        query.setParameter("end", endDate);
        return query.getResultList();
    }

    public List<Order> findIncompleteOrders() {
        TypedQuery<Order> query = entityManager.createQuery(
                "SELECT o FROM Order o WHERE o.status IN ('PENDING', 'PROCESSING')",
                Order.class
        );
        return query.getResultList();
    }
}
