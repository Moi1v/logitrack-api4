package com.mcabrera.logitrackapi.controllers;

import com.mcabrera.logitrackapi.dtos.CreateOrderDto;
import com.mcabrera.logitrackapi.dtos.OrderResponseDto;
import com.mcabrera.logitrackapi.models.Order;
import com.mcabrera.logitrackapi.services.OrderService;
import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Path("/orders")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class OrderController {

    @Inject
    private OrderService orderService;

    @GET
    public Response getAllOrders() {
        List<Order> orders = orderService.getAll();
        List<OrderResponseDto> orderDtos = orders.stream()
                .map(orderService::toOrderResponseDto)
                .collect(Collectors.toList());
        return Response.ok(orderDtos).build();
    }

    @GET
    @Path("/{id}")
    public Response getOrderById(@PathParam("id") Long id) {
        Optional<Order> order = orderService.findById(id);
        if (order.isEmpty()) {
            return Response.status(Response.Status.NOT_FOUND)
                    .entity("Orden no encontrada")
                    .build();
        }
        OrderResponseDto orderDto = orderService.toOrderResponseDto(order.get());
        return Response.ok(orderDto).build();
    }

    @GET
    @Path("/customer/{customerId}")
    public Response getOrdersByCustomer(@PathParam("customerId") Long customerId) {
        List<Order> orders = orderService.findByCustomerId(customerId);
        List<OrderResponseDto> orderDtos = orders.stream()
                .map(orderService::toOrderResponseDto)
                .collect(Collectors.toList());
        return Response.ok(orderDtos).build();
    }

    @GET
    @Path("/status/{status}")
    public Response getOrdersByStatus(@PathParam("status") String status) {
        List<Order> orders = orderService.findByStatus(status);
        List<OrderResponseDto> orderDtos = orders.stream()
                .map(orderService::toOrderResponseDto)
                .collect(Collectors.toList());
        return Response.ok(orderDtos).build();
    }

    @GET
    @Path("/incomplete")
    public Response getIncompleteOrders() {
        List<Order> orders = orderService.findIncompleteOrders();
        List<OrderResponseDto> orderDtos = orders.stream()
                .map(orderService::toOrderResponseDto)
                .collect(Collectors.toList());
        return Response.ok(orderDtos).build();
    }

    @GET
    @Path("/customer/{customerId}/debt")
    public Response getCustomerDebt(@PathParam("customerId") Long customerId) {
        BigDecimal debt = orderService.getTotalDebtByCustomer(customerId);
        return Response.ok().entity(new DebtResponse(customerId, debt)).build();
    }

    @POST
    public Response createOrder(CreateOrderDto orderDto) {
        // Validaciones básicas
        if (orderDto.getCustomerId() == null) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity("El ID del cliente es requerido")
                    .build();
        }

        if (orderDto.getItems() == null || orderDto.getItems().isEmpty()) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity("La orden debe contener al menos un producto")
                    .build();
        }

        Optional<Order> createdOrder = orderService.createOrder(orderDto);
        if (createdOrder.isEmpty()) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity("No se pudo crear la orden. Verifique que el cliente esté activo y los productos existan")
                    .build();
        }

        OrderResponseDto orderResponse = orderService.toOrderResponseDto(createdOrder.get());
        return Response.status(Response.Status.CREATED)
                .entity(orderResponse)
                .build();
    }

    @PUT
    @Path("/{id}/status")
    public Response updateOrderStatus(@PathParam("id") Long id, StatusUpdateRequest statusRequest) {
        if (statusRequest.getStatus() == null || statusRequest.getStatus().isBlank()) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity("El estado es requerido")
                    .build();
        }

        Optional<Order> updatedOrder = orderService.updateOrderStatus(id, statusRequest.getStatus());
        if (updatedOrder.isEmpty()) {
            return Response.status(Response.Status.NOT_FOUND)
                    .entity("Orden no encontrada")
                    .build();
        }

        OrderResponseDto orderResponse = orderService.toOrderResponseDto(updatedOrder.get());
        return Response.ok(orderResponse).build();
    }

    // Clases internas para requests/responses
    public static class StatusUpdateRequest {
        private String status;

        public String getStatus() {
            return status;
        }

        public void setStatus(String status) {
            this.status = status;
        }
    }

    public static class DebtResponse {
        private Long customerId;
        private BigDecimal totalDebt;

        public DebtResponse(Long customerId, BigDecimal totalDebt) {
            this.customerId = customerId;
            this.totalDebt = totalDebt;
        }

        public Long getCustomerId() {
            return customerId;
        }

        public void setCustomerId(Long customerId) {
            this.customerId = customerId;
        }

        public BigDecimal getTotalDebt() {
            return totalDebt;
        }

        public void setTotalDebt(BigDecimal totalDebt) {
            this.totalDebt = totalDebt;
        }
    }
}