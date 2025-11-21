package com.darwinruiz.logitrack.repositories;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.persistence.TypedQuery;

import java.util.List;

@ApplicationScoped
public class PaymentRepository extends BaseRepository<Payment, Long> {

    @Override
    protected Class<Payment> entity() {
        return Payment.class;
    }

    public List<Payment> findByOrderId(Long orderId) {
        TypedQuery<Payment> query = entityManager.createQuery(
                "SELECT p FROM Payment p WHERE p.order.orderId = :orderId ORDER BY p.paymentDate DESC",
                Payment.class
        );
        query.setParameter("orderId", orderId);
        return query.getResultList();
    }
}
