package com.darwinruiz.logitrack.repositories;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.persistence.TypedQuery;

@ApplicationScoped
public class CustomerRepository extends BaseRepository<Customer, Long> {

    @Override
    protected Class<Customer> entity() {
        return Customer.class;
    }

    public Optional<Customer> findByTaxId(String taxId) {
        try {
            TypedQuery<Customer> query = entityManager.createQuery(
                    "SELECT c FROM Customer c WHERE c.taxId = :taxId",
                    Customer.class
            );
            query.setParameter("taxId", taxId);
            return Optional.ofNullable(query.getSingleResult());
        } catch (Exception e) {
            return Optional.empty();
        }
    }
}
