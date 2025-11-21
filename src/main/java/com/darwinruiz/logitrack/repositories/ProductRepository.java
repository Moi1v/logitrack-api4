// ProductRepository.java
package com.darwinruiz.logitrack.repositories;

import com.darwinruiz.logitrack.models.Product;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.persistence.TypedQuery;
import java.util.List;

@ApplicationScoped
public class ProductRepository extends BaseRepository<Product, Long> {

    @Override
    protected Class<Product> entity() {
        return Product.class;
    }

    public List<Product> findByCategory(String category) {
        TypedQuery<Product> query = entityManager.createQuery(
                "SELECT p FROM Product p WHERE p.category = :category",
                Product.class
        );
        query.setParameter("category", category);
        return query.getResultList();
    }

    public List<Product> findActiveProducts() {
        TypedQuery<Product> query = entityManager.createQuery(
                "SELECT p FROM Product p WHERE p.active = true",
                Product.class
        );
        return query.getResultList();
    }
}