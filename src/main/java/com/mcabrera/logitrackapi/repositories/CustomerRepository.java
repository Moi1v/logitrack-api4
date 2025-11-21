package com.mcabrera.logitrackapi.repositories;

import com.mcabrera.logitrackapi.models.Customer;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.persistence.TypedQuery;
import java.util.List;
import java.util.Optional;

@ApplicationScoped
public class CustomerRepository extends BaseRepository<Customer, Long> {

    @Override
    protected Class<Customer> entity() {
        return Customer.class;
    }

    public Optional<Customer> findByTaxId(String taxId) {
        try {
            TypedQuery<Customer> query = entityManager.createQuery(
                    "SELECT c FROM Customer c WHERE c.taxId = :taxId", Customer.class);
            query.setParameter("taxId", taxId);
            return Optional.ofNullable(query.getSingleResult());
        } catch (Exception e) {
            return Optional.empty();
        }
    }

    public List<Customer> findActiveCustomers() {
        return entityManager.createQuery(
                        "SELECT c FROM Customer c WHERE c.active = true ORDER BY c.fullName", Customer.class)
                .getResultList();
    }

    public Optional<Customer> findByEmail(String email) {
        try {
            TypedQuery<Customer> query = entityManager.createQuery(
                    "SELECT c FROM Customer c WHERE c.email = :email", Customer.class);
            query.setParameter("email", email);
            return Optional.ofNullable(query.getSingleResult());
        } catch (Exception e) {
            return Optional.empty();
        }
    }
}