package com.mcabrera.logitrackapi.services;

import com.mcabrera.logitrackapi.dtos.PaymentDto;
import com.mcabrera.logitrackapi.models.Order;
import com.mcabrera.logitrackapi.models.Payment;
import com.mcabrera.logitrackapi.repositories.OrderRepository;
import com.mcabrera.logitrackapi.repositories.PaymentRepository;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@ApplicationScoped
public class PaymentService {

    @Inject
    private PaymentRepository paymentRepository;

    @Inject
    private OrderRepository orderRepository;

    @Inject
    private OrderService orderService;

    public List<Payment> getAll() {
        return paymentRepository.getAll();
    }

    public Optional<Payment> findById(Long id) {
        return paymentRepository.findById(id);
    }

    public List<Payment> findByOrderId(Long orderId) {
        return paymentRepository.findByOrderId(orderId);
    }

    @Transactional
    public Optional<Payment> processPayment(PaymentDto paymentDto) {
        // Validar que la orden exista
        Optional<Order> orderOpt = orderRepository.findById(paymentDto.getOrderId());
        if (orderOpt.isEmpty()) {
            return Optional.empty();
        }

        Order order = orderOpt.get();

        // Validar que el monto no sea negativo o cero
        if (paymentDto.getAmount() == null || paymentDto.getAmount().compareTo(BigDecimal.ZERO) <= 0) {
            return Optional.empty();
        }

        // Calcular total pagado hasta ahora
        BigDecimal totalPaid = paymentRepository.getTotalPaidByOrder(order.getOrderId());
        BigDecimal pendingAmount = order.getTotalAmount().subtract(totalPaid);

        // Validar que el pago no exceda el monto pendiente
        if (paymentDto.getAmount().compareTo(pendingAmount) > 0) {
            return Optional.empty();
        }

        // Validar método de pago
        if (paymentDto.getMethod() == null ||
                !List.of("Cash", "Card", "Transfer").contains(paymentDto.getMethod())) {
            return Optional.empty();
        }

        // Crear y guardar el pago
        Payment payment = new Payment();
        payment.setOrder(order);
        payment.setAmount(paymentDto.getAmount());
        payment.setMethod(paymentDto.getMethod());

        Optional<Payment> savedPayment = paymentRepository.save(payment);

        // Actualizar estado de la orden si está completamente pagada
        if (savedPayment.isPresent()) {
            BigDecimal newTotalPaid = totalPaid.add(paymentDto.getAmount());
            if (newTotalPaid.compareTo(order.getTotalAmount()) >= 0 &&
                    !"Completed".equals(order.getStatus())) {
                orderService.updateOrderStatus(order.getOrderId(), "Completed");
            }
        }

        return savedPayment;
    }

    public PaymentDto toPaymentDto(Payment payment) {
        PaymentDto dto = new PaymentDto();
        dto.setPaymentId(payment.getPaymentId());
        dto.setOrderId(payment.getOrder().getOrderId());
        dto.setPaymentDate(payment.getPaymentDate());
        dto.setAmount(payment.getAmount());
        dto.setMethod(payment.getMethod());
        return dto;
    }

    public BigDecimal getTotalPaidByOrder(Long orderId) {
        return paymentRepository.getTotalPaidByOrder(orderId);
    }
}