package com.mcabrera.logitrackapi.repositories;

import com.mcabrera.logitrackapi.models.OrderItem;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.persistence.TypedQuery;

import java.math.BigDecimal;
import java.util.List;

@ApplicationScoped
public class OrderItemRepository extends BaseRepository<OrderItem, Long> {

    @Override
    protected Class<OrderItem> entity() {
        return OrderItem.class;
    }

    public List<OrderItem> findByOrderId(Long orderId) {
        return entityManager.createQuery(
                        "SELECT oi FROM OrderItem oi WHERE oi.order.orderId = :orderId", OrderItem.class)
                .setParameter("orderId", orderId)
                .getResultList();
    }

    public void deleteByOrderId(Long orderId) {
        entityManager.createQuery("DELETE FROM OrderItem oi WHERE oi.order.orderId = :orderId")
                .setParameter("orderId", orderId)
                .executeUpdate();
    }

    // Validar si el producto existe y está activo
    public boolean isValidActiveProduct(Long productId) {
        try {
            TypedQuery<Long> query = entityManager.createQuery(
                    "SELECT COUNT(p) FROM Product p WHERE p.productId = :productId AND p.active = true", Long.class);
            query.setParameter("productId", productId);
            return query.getSingleResult() > 0;
        } catch (Exception e) {
            return false;
        }
    }

    // Calcular total de una orden
    public BigDecimal calculateOrderTotal(Long orderId) {
        try {
            TypedQuery<BigDecimal> query = entityManager.createQuery(
                    "SELECT SUM(oi.subtotal) FROM OrderItem oi WHERE oi.order.orderId = :orderId", BigDecimal.class);
            query.setParameter("orderId", orderId);
            BigDecimal result = query.getSingleResult();
            return result != null ? result : BigDecimal.ZERO;
        } catch (Exception e) {
            return BigDecimal.ZERO;
        }
    }
}