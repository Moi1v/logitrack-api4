package com.mcabrera.logitrackapi.services;

import com.mcabrera.logitrackapi.models.Product;
import com.mcabrera.logitrackapi.repositories.ProductRepository;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import java.util.List;
import java.util.Optional;

@ApplicationScoped
public class ProductService {

    @Inject
    private ProductRepository productRepository;

    public List<Product> getAll() {
        return productRepository.getAll();
    }

    public List<Product> getActiveProducts() {
        return productRepository.findActiveProducts();
    }

    public List<Product> getProductsByCategory(String category) {
        return productRepository.findByCategory(category);
    }

    public Optional<Product> findById(Long id) {
        return productRepository.findById(id);
    }

    public Optional<Product> findByName(String name) {
        return productRepository.findByName(name);
    }

    public Optional<Product> save(Product product) {
        return productRepository.save(product);
    }

    public void delete(Product product) {
        productRepository.delete(product);
    }

    public void deactivateProduct(Long productId) {
        Optional<Product> product = findById(productId);
        if (product.isPresent()) {
            Product p = product.get();
            p.setActive(false);
            save(p);
        }
    }

    public void activateProduct(Long productId) {
        Optional<Product> product = findById(productId);
        if (product.isPresent()) {
            Product p = product.get();
            p.setActive(true);
            save(p);
        }
    }
}