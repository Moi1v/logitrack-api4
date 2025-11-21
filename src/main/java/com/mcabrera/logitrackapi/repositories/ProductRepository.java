package com.mcabrera.logitrackapi.repositories;

import com.mcabrera.logitrackapi.dtos.ProductStatsDto;
import com.mcabrera.logitrackapi.models.Product;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.persistence.TypedQuery;
import java.util.List;
import java.util.Optional;

@ApplicationScoped
public class ProductRepository extends BaseRepository<Product, Long> {

    @Override
    protected Class<Product> entity() {
        return Product.class;
    }

    public List<Product> findActiveProducts() {
        return entityManager.createQuery(
                        "SELECT p FROM Product p WHERE p.active = true ORDER BY p.name", Product.class)
                .getResultList();
    }

    public List<Product> findByCategory(String category) {
        return entityManager.createQuery(
                        "SELECT p FROM Product p WHERE p.category = :category AND p.active = true", Product.class)
                .setParameter("category", category)
                .getResultList();
    }

    public Optional<Product> findByName(String name) {
        try {
            TypedQuery<Product> query = entityManager.createQuery(
                    "SELECT p FROM Product p WHERE p.name = :name", Product.class);
            query.setParameter("name", name);
            return Optional.ofNullable(query.getSingleResult());
        } catch (Exception e) {
            return Optional.empty();
        }
    }

    /**
     * Obtiene los productos más vendidos
     * @param limit Número máximo de productos a retornar
     * @return Lista de productos más vendidos con sus estadísticas
     */
    public List<ProductStatsDto> findTopSellingProducts(int limit) {
        String jpql = "SELECT new com.mcabrera.logitrackapi.dtos.ProductStatsDto(" +
                "p.productId, " +
                "p.name, " +
                "p.category, " +
                "SUM(oi.quantity), " +
                "SUM(oi.subtotal), " +
                "COUNT(DISTINCT o.orderId)) " +
                "FROM OrderItem oi " +
                "JOIN oi.product p " +
                "JOIN oi.order o " +
                "WHERE o.status != 'Cancelled' " +
                "GROUP BY p.productId, p.name, p.category " +
                "ORDER BY SUM(oi.quantity) DESC";

        TypedQuery<ProductStatsDto> query = entityManager.createQuery(jpql, ProductStatsDto.class);
        query.setMaxResults(limit);

        return query.getResultList();
    }

    /**
     * Obtiene productos más vendidos por categoría
     * @param category Categoría del producto
     * @param limit Número máximo de productos a retornar
     * @return Lista de productos más vendidos de la categoría
     */
    public List<ProductStatsDto> findTopSellingProductsByCategory(String category, int limit) {
        String jpql = "SELECT new com.mcabrera.logitrackapi.dtos.ProductStatsDto(" +
                "p.productId, " +
                "p.name, " +
                "p.category, " +
                "SUM(oi.quantity), " +
                "SUM(oi.subtotal), " +
                "COUNT(DISTINCT o.orderId)) " +
                "FROM OrderItem oi " +
                "JOIN oi.product p " +
                "JOIN oi.order o " +
                "WHERE o.status != 'Cancelled' AND p.category = :category " +
                "GROUP BY p.productId, p.name, p.category " +
                "ORDER BY SUM(oi.quantity) DESC";

        TypedQuery<ProductStatsDto> query = entityManager.createQuery(jpql, ProductStatsDto.class);
        query.setParameter("category", category);
        query.setMaxResults(limit);

        return query.getResultList();
    }

    /**
     * Obtiene el total de ventas por producto
     * @param productId ID del producto
     * @return Estadísticas de ventas del producto
     */
    public Optional<ProductStatsDto> getProductSalesStats(Long productId) {
        try {
            String jpql = "SELECT new com.mcabrera.logitrackapi.dtos.ProductStatsDto(" +
                    "p.productId, " +
                    "p.name, " +
                    "p.category, " +
                    "COALESCE(SUM(oi.quantity), 0), " +
                    "COALESCE(SUM(oi.subtotal), 0), " +
                    "COUNT(DISTINCT o.orderId)) " +
                    "FROM Product p " +
                    "LEFT JOIN OrderItem oi ON oi.product.productId = p.productId " +
                    "LEFT JOIN Order o ON oi.order.orderId = o.orderId AND o.status != 'Cancelled' " +
                    "WHERE p.productId = :productId " +
                    "GROUP BY p.productId, p.name, p.category";

            TypedQuery<ProductStatsDto> query = entityManager.createQuery(jpql, ProductStatsDto.class);
            query.setParameter("productId", productId);

            return Optional.ofNullable(query.getSingleResult());
        } catch (Exception e) {
            return Optional.empty();
        }
    }
}