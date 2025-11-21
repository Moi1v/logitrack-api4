package com.mcabrera.logitrackapi.repositories;

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
}