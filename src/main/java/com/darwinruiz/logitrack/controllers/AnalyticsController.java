package com.darwinruiz.logitrack.controllers;

import jakarta.inject.Inject;
import jakarta.persistence.EntityManager;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Path("/analytics")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class AnalyticsController {

    @Inject
    private EntityManager entityManager;

    /**
     * Obtener el total adeudado por cliente
     * Calcula la suma de órdenes menos pagos por cada cliente
     */
    @GET
    @Path("/customer-debt")
    public Response getCustomerDebt() {
        String query = """
            SELECT 
                c.customerId as customerId,
                c.fullName as customerName,
                COALESCE(SUM(o.totalAmount), 0) as totalOrders,
                COALESCE(SUM(p.amount), 0) as totalPaid,
                COALESCE(SUM(o.totalAmount), 0) - COALESCE(SUM(p.amount), 0) as debt
            FROM Customer c
            LEFT JOIN c.orders o
            LEFT JOIN o.payments p
            WHERE c.active = true
            GROUP BY c.customerId, c.fullName
            HAVING (COALESCE(SUM(o.totalAmount), 0) - COALESCE(SUM(p.amount), 0)) > 0
            ORDER BY debt DESC
        """;

        List<Object[]> results = entityManager.createQuery(query, Object[].class)
                .getResultList();

        List<Map<String, Object>> response = results.stream()
                .map(row -> {
                    Map<String, Object> map = new HashMap<>();
                    map.put("customerId", row[0]);
                    map.put("customerName", row[1]);
                    map.put("totalOrders", row[2]);
                    map.put("totalPaid", row[3]);
                    map.put("debt", row[4]);
                    return map;
                })
                .toList();

        return Response.ok(response).build();
    }

    /**
     * Listar productos más vendidos
     * Ordena productos por cantidad total vendida
     */
    @GET
    @Path("/top-products")
    public Response getTopProducts(@QueryParam("limit") @DefaultValue("10") int limit) {
        String query = """
            SELECT 
                p.productId as productId,
                p.name as productName,
                p.category as category,
                SUM(oi.quantity) as totalSold,
                SUM(oi.subtotal) as totalRevenue
            FROM Product p
            JOIN p.orderItems oi
            GROUP BY p.productId, p.name, p.category
            ORDER BY totalSold DESC
        """;

        List<Object[]> results = entityManager.createQuery(query, Object[].class)
                .setMaxResults(limit)
                .getResultList();

        List<Map<String, Object>> response = results.stream()
                .map(row -> {
                    Map<String, Object> map = new HashMap<>();
                    map.put("productId", row[0]);
                    map.put("productName", row[1]);
                    map.put("category", row[2]);
                    map.put("totalSold", row[3]);
                    map.put("totalRevenue", row[4]);
                    return map;
                })
                .toList();

        return Response.ok(response).build();
    }

    /**
     * Obtener estadísticas generales del negocio
     */
    @GET
    @Path("/business-stats")
    public Response getBusinessStats() {
        Map<String, Object> stats = new HashMap<>();

        // Total de productos activos
        Long activeProducts = entityManager.createQuery(
                        "SELECT COUNT(p) FROM Product p WHERE p.active = true", Long.class)
                .getSingleResult();
        stats.put("activeProducts", activeProducts);

        // Total de clientes activos
        Long activeCustomers = entityManager.createQuery(
                        "SELECT COUNT(c) FROM Customer c WHERE c.active = true", Long.class)
                .getSingleResult();
        stats.put("activeCustomers", activeCustomers);

        // Total de órdenes
        Long totalOrders = entityManager.createQuery(
                        "SELECT COUNT(o) FROM Order o", Long.class)
                .getSingleResult();
        stats.put("totalOrders", totalOrders);

        // Órdenes pendientes
        Long pendingOrders = entityManager.createQuery(
                        "SELECT COUNT(o) FROM Order o WHERE o.status IN ('PENDING', 'PROCESSING')", Long.class)
                .getSingleResult();
        stats.put("pendingOrders", pendingOrders);

        // Ingresos totales
        BigDecimal totalRevenue = entityManager.createQuery(
                        "SELECT COALESCE(SUM(p.amount), 0) FROM Payment p", BigDecimal.class)
                .getSingleResult();
        stats.put("totalRevenue", totalRevenue);

        // Deuda total pendiente
        BigDecimal totalDebt = entityManager.createQuery(
                        "SELECT COALESCE(SUM(o.totalAmount), 0) - COALESCE(SUM(p.amount), 0) FROM Order o LEFT JOIN o.payments p",
                        BigDecimal.class)
                .getSingleResult();
        stats.put("totalDebt", totalDebt);

        return Response.ok(stats).build();
    }

    /**
     * Obtener ventas por categoría
     */
    @GET
    @Path("/sales-by-category")
    public Response getSalesByCategory() {
        String query = """
            SELECT 
                p.category as category,
                COUNT(DISTINCT oi.order.orderId) as orderCount,
                SUM(oi.quantity) as totalQuantity,
                SUM(oi.subtotal) as totalRevenue
            FROM Product p
            JOIN p.orderItems oi
            WHERE p.active = true
            GROUP BY p.category
            ORDER BY totalRevenue DESC
        """;

        List<Object[]> results = entityManager.createQuery(query, Object[].class)
                .getResultList();

        List<Map<String, Object>> response = results.stream()
                .map(row -> {
                    Map<String, Object> map = new HashMap<>();
                    map.put("category", row[0]);
                    map.put("orderCount", row[1]);
                    map.put("totalQuantity", row[2]);
                    map.put("totalRevenue", row[3]);
                    return map;
                })
                .toList();

        return Response.ok(response).build();
    }
}