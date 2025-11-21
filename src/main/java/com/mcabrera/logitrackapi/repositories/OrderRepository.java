package com.mcabrera.logitrackapi.repositories;

import com.mcabrera.logitrackapi.models.Order;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.persistence.TypedQuery;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@ApplicationScoped
public class OrderRepository extends BaseRepository<Order, Long> {

    @Override
    protected Class<Order> entity() {
        return Order.class;
    }

    public List<Order> findByCustomerId(Long customerId) {
        return entityManager.createQuery(
                        "SELECT o FROM Order o WHERE o.customer.customerId = :customerId ORDER BY o.orderDate DESC", Order.class)
                .setParameter("customerId", customerId)
                .getResultList();
    }

    public List<Order> findByStatus(String status) {
        return entityManager.createQuery(
                        "SELECT o FROM Order o WHERE o.status = :status ORDER BY o.orderDate DESC", Order.class)
                .setParameter("status", status)
                .getResultList();
    }

    public List<Order> findByDateRange(LocalDateTime startDate, LocalDateTime endDate) {
        return entityManager.createQuery(
                        "SELECT o FROM Order o WHERE o.orderDate BETWEEN :startDate AND :endDate ORDER BY o.orderDate DESC", Order.class)
                .setParameter("startDate", startDate)
                .setParameter("endDate", endDate)
                .getResultList();
    }

    public List<Order> findIncompleteOrders() {
        return entityManager.createQuery(
                        "SELECT o FROM Order o WHERE o.status IN ('Pending', 'Processing') ORDER BY o.orderDate ASC", Order.class)
                .getResultList();
    }

    public BigDecimal getTotalDebtByCustomer(Long customerId) {
        try {
            TypedQuery<BigDecimal> query = entityManager.createQuery(
                    "SELECT SUM(o.totalAmount) - COALESCE(SUM(p.amount), 0) " +
                            "FROM Order o LEFT JOIN o.payments p " +
                            "WHERE o.customer.customerId = :customerId AND o.status != 'Cancelled'", BigDecimal.class);
            query.setParameter("customerId", customerId);
            BigDecimal result = query.getSingleResult();
            return result != null ? result : BigDecimal.ZERO;
        } catch (Exception e) {
            return BigDecimal.ZERO;
        }
    }
}