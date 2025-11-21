package com.mcabrera.logitrackapi.repositories;

import com.mcabrera.logitrackapi.models.Payment;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.persistence.TypedQuery;
import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@ApplicationScoped
public class PaymentRepository extends BaseRepository<Payment, Long> {

    @Override
    protected Class<Payment> entity() {
        return Payment.class;
    }

    public List<Payment> findByOrderId(Long orderId) {
        return entityManager.createQuery(
                        "SELECT p FROM Payment p WHERE p.order.orderId = :orderId ORDER BY p.paymentDate DESC", Payment.class)
                .setParameter("orderId", orderId)
                .getResultList();
    }

    public BigDecimal getTotalPaidByOrder(Long orderId) {
        try {
            TypedQuery<BigDecimal> query = entityManager.createQuery(
                    "SELECT COALESCE(SUM(p.amount), 0) FROM Payment p WHERE p.order.orderId = :orderId", BigDecimal.class);
            query.setParameter("orderId", orderId);
            return query.getSingleResult();
        } catch (Exception e) {
            return BigDecimal.ZERO;
        }
    }

    public Optional<Payment> findLatestByOrder(Long orderId) {
        try {
            TypedQuery<Payment> query = entityManager.createQuery(
                    "SELECT p FROM Payment p WHERE p.order.orderId = :orderId ORDER BY p.paymentDate DESC", Payment.class);
            query.setParameter("orderId", orderId);
            query.setMaxResults(1);
            return Optional.ofNullable(query.getSingleResult());
        } catch (Exception e) {
            return Optional.empty();
        }
    }
}