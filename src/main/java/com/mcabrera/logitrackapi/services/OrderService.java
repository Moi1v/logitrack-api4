package com.mcabrera.logitrackapi.services;

import com.mcabrera.logitrackapi.dtos.CreateOrderDto;
import com.mcabrera.logitrackapi.dtos.OrderItemRequestDto;
import com.mcabrera.logitrackapi.dtos.OrderResponseDto;
import com.mcabrera.logitrackapi.dtos.OrderItemDto;
import com.mcabrera.logitrackapi.models.Customer;
import com.mcabrera.logitrackapi.models.Order;
import com.mcabrera.logitrackapi.models.OrderItem;
import com.mcabrera.logitrackapi.models.Product;
import com.mcabrera.logitrackapi.repositories.OrderRepository;
import com.mcabrera.logitrackapi.repositories.PaymentRepository;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@ApplicationScoped
public class OrderService {

    @Inject
    private OrderRepository orderRepository;

    @Inject
    private CustomerService customerService;

    @Inject
    private ProductService productService;

    @Inject
    private PaymentRepository paymentRepository;

    public List<Order> getAll() {
        return orderRepository.getAll();
    }

    public Optional<Order> findById(Long id) {
        return orderRepository.findById(id);
    }

    public List<Order> findByCustomerId(Long customerId) {
        return orderRepository.findByCustomerId(customerId);
    }

    public List<Order> findByStatus(String status) {
        return orderRepository.findByStatus(status);
    }

    public List<Order> findIncompleteOrders() {
        return orderRepository.findIncompleteOrders();
    }

    @Transactional
    public Optional<Order> createOrder(CreateOrderDto orderDto) {
        // Validar que el cliente exista y esté activo
        Optional<Customer> customer = customerService.findById(orderDto.getCustomerId());
        if (customer.isEmpty() || !customerService.isCustomerActive(orderDto.getCustomerId())) {
            return Optional.empty();
        }

        // Crear la orden
        Order order = new Order();
        order.setCustomer(customer.get());
        order.setStatus("Pending");

        // Procesar items
        List<OrderItem> items = new ArrayList<>();
        for (OrderItemRequestDto itemDto : orderDto.getItems()) {
            Optional<Product> product = productService.findById(itemDto.getProductId());
            if (product.isEmpty() || !product.get().getActive()) {
                return Optional.empty(); // Producto no encontrado o inactivo
            }

            if (itemDto.getQuantity() <= 0) {
                return Optional.empty(); // Cantidad inválida
            }

            OrderItem orderItem = new OrderItem();
            orderItem.setProduct(product.get());
            orderItem.setQuantity(itemDto.getQuantity());
            orderItem.setUnitPrice(product.get().getPrice());
            orderItem.calculateSubtotal();

            items.add(orderItem);
            order.addItem(orderItem);
        }

        // Calcular total
        order.calculateTotal();

        // Guardar la orden
        return orderRepository.save(order);
    }

    @Transactional
    public Optional<Order> updateOrderStatus(Long orderId, String status) {
        Optional<Order> orderOpt = orderRepository.findById(orderId);
        if (orderOpt.isEmpty()) {
            return Optional.empty();
        }

        Order order = orderOpt.get();
        order.setStatus(status);
        return orderRepository.save(order);
    }

    public OrderResponseDto toOrderResponseDto(Order order) {
        OrderResponseDto dto = new OrderResponseDto();
        dto.setOrderId(order.getOrderId());
        dto.setCustomerId(order.getCustomer().getCustomerId());
        dto.setCustomerName(order.getCustomer().getFullName());
        dto.setOrderDate(order.getOrderDate());
        dto.setStatus(order.getStatus());
        dto.setTotalAmount(order.getTotalAmount());

        // Convertir items a DTO
        List<OrderItemDto> itemDtos = order.getItems().stream()
                .map(item -> new OrderItemDto(
                        item.getProduct().getProductId(),
                        item.getProduct().getName(),
                        item.getQuantity(),
                        item.getUnitPrice(),
                        item.getSubtotal()
                ))
                .collect(Collectors.toList());
        dto.setItems(itemDtos);

        // Calcular montos de pago
        BigDecimal paidAmount = paymentRepository.getTotalPaidByOrder(order.getOrderId());
        dto.setPaidAmount(paidAmount);
        dto.setPendingAmount(order.getTotalAmount().subtract(paidAmount));

        return dto;
    }

    public BigDecimal getTotalDebtByCustomer(Long customerId) {
        return orderRepository.getTotalDebtByCustomer(customerId);
    }
}