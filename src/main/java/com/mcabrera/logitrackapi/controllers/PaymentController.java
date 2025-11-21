package com.mcabrera.logitrackapi.controllers;

import com.mcabrera.logitrackapi.dtos.PaymentDto;
import com.mcabrera.logitrackapi.models.Payment;
import com.mcabrera.logitrackapi.services.PaymentService;
import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Path("/payments")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class PaymentController {

    @Inject
    private PaymentService paymentService;

    @GET
    public Response getAllPayments() {
        List<Payment> payments = paymentService.getAll();
        List<PaymentDto> paymentDtos = payments.stream()
                .map(paymentService::toPaymentDto)
                .collect(Collectors.toList());
        return Response.ok(paymentDtos).build();
    }

    @GET
    @Path("/{id}")
    public Response getPaymentById(@PathParam("id") Long id) {
        Optional<Payment> payment = paymentService.findById(id);
        if (payment.isEmpty()) {
            return Response.status(Response.Status.NOT_FOUND)
                    .entity("Pago no encontrado")
                    .build();
        }
        PaymentDto paymentDto = paymentService.toPaymentDto(payment.get());
        return Response.ok(paymentDto).build();
    }

    @GET
    @Path("/order/{orderId}")
    public Response getPaymentsByOrder(@PathParam("orderId") Long orderId) {
        List<Payment> payments = paymentService.findByOrderId(orderId);
        List<PaymentDto> paymentDtos = payments.stream()
                .map(paymentService::toPaymentDto)
                .collect(Collectors.toList());
        return Response.ok(paymentDtos).build();
    }

    @POST
    public Response processPayment(PaymentDto paymentDto) {
        // Validaciones básicas
        if (paymentDto.getOrderId() == null) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity("El ID de la orden es requerido")
                    .build();
        }

        if (paymentDto.getAmount() == null) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity("El monto del pago es requerido")
                    .build();
        }

        if (paymentDto.getMethod() == null || paymentDto.getMethod().isBlank()) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity("El método de pago es requerido")
                    .build();
        }

        Optional<Payment> processedPayment = paymentService.processPayment(paymentDto);
        if (processedPayment.isEmpty()) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity("No se pudo procesar el pago. Verifique la orden, el monto y el método de pago")
                    .build();
        }

        PaymentDto responseDto = paymentService.toPaymentDto(processedPayment.get());
        return Response.status(Response.Status.CREATED)
                .entity(responseDto)
                .build();
    }

    @GET
    @Path("/order/{orderId}/total-paid")
    public Response getTotalPaidByOrder(@PathParam("orderId") Long orderId) {
        BigDecimal totalPaid = paymentService.getTotalPaidByOrder(orderId);
        return Response.ok().entity(new TotalPaidResponse(orderId, totalPaid)).build();
    }

    // Clase interna para response
    public static class TotalPaidResponse {
        private Long orderId;
        private BigDecimal totalPaid;

        public TotalPaidResponse(Long orderId, BigDecimal totalPaid) {
            this.orderId = orderId;
            this.totalPaid = totalPaid;
        }

        public Long getOrderId() {
            return orderId;
        }

        public void setOrderId(Long orderId) {
            this.orderId = orderId;
        }

        public BigDecimal getTotalPaid() {
            return totalPaid;
        }

        public void setTotalPaid(BigDecimal totalPaid) {
            this.totalPaid = totalPaid;
        }
    }
}