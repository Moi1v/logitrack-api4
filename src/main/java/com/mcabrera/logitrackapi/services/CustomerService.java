package com.mcabrera.logitrackapi.services;

import com.mcabrera.logitrackapi.models.Customer;
import com.mcabrera.logitrackapi.repositories.CustomerRepository;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import java.util.List;
import java.util.Optional;

@ApplicationScoped
public class CustomerService {

    @Inject
    private CustomerRepository customerRepository;

    public List<Customer> getAll() {
        return customerRepository.getAll();
    }

    public List<Customer> getActiveCustomers() {
        return customerRepository.findActiveCustomers();
    }

    public Optional<Customer> findById(Long id) {
        return customerRepository.findById(id);
    }

    public Optional<Customer> findByTaxId(String taxId) {
        return customerRepository.findByTaxId(taxId);
    }

    public Optional<Customer> findByEmail(String email) {
        return customerRepository.findByEmail(email);
    }

    public Optional<Customer> save(Customer customer) {
        // Verificar si es una actualización o creación nueva
        if (customer.getCustomerId() != null) {
            // Es una actualización - verificar duplicados en OTROS clientes

            // Verificar email
            if (customer.getEmail() != null) {
                Optional<Customer> existingByEmail = customerRepository.findByEmail(customer.getEmail());
                if (existingByEmail.isPresent() &&
                        !existingByEmail.get().getCustomerId().equals(customer.getCustomerId())) {
                    return Optional.empty();
                }
            }

            // Verificar taxId
            if (customer.getTaxId() != null) {
                Optional<Customer> existingByTaxId = customerRepository.findByTaxId(customer.getTaxId());
                if (existingByTaxId.isPresent() &&
                        !existingByTaxId.get().getCustomerId().equals(customer.getCustomerId())) {
                    return Optional.empty();
                }
            }
        } else {
            // Es creación nueva - verificar si email o taxId ya existen
            if (customer.getEmail() != null) {
                Optional<Customer> existingByEmail = customerRepository.findByEmail(customer.getEmail());
                if (existingByEmail.isPresent()) {
                    return Optional.empty();
                }
            }

            if (customer.getTaxId() != null) {
                Optional<Customer> existingByTaxId = customerRepository.findByTaxId(customer.getTaxId());
                if (existingByTaxId.isPresent()) {
                    return Optional.empty();
                }
            }
        }

        return customerRepository.save(customer);
    }

    public void delete(Customer customer) {
        customerRepository.delete(customer);
    }

    public void deactivateCustomer(Long customerId) {
        Optional<Customer> customer = findById(customerId);
        if (customer.isPresent()) {
            Customer c = customer.get();
            c.setActive(false);
            save(c);
        }
    }

    public void activateCustomer(Long customerId) {
        Optional<Customer> customer = findById(customerId);
        if (customer.isPresent()) {
            Customer c = customer.get();
            c.setActive(true);
            save(c);
        }
    }

    public boolean isCustomerActive(Long customerId) {
        Optional<Customer> customer = findById(customerId);
        return customer.isPresent() && Boolean.TRUE.equals(customer.get().getActive());
    }
}