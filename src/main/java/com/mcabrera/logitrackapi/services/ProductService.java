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
        try {
            System.out.println("=== ProductService.save() ===");
            System.out.println("Product ID: " + product.getProductId());
            System.out.println("Product Name: " + product.getName());

            // Verificar si es una actualización o creación nueva
            if (product.getProductId() != null) {
                System.out.println("Modo: ACTUALIZACIÓN");
                // Es una actualización - verificar si el nombre ya existe en OTRO producto
                Optional<Product> existingByName = productRepository.findByName(product.getName());
                if (existingByName.isPresent() &&
                        !existingByName.get().getProductId().equals(product.getProductId())) {
                    System.err.println("ERROR: El nombre ya existe en otro producto");
                    return Optional.empty();
                }
            } else {
                System.out.println("Modo: CREACIÓN NUEVA");
                // Es creación nueva - verificar si el nombre ya existe
                Optional<Product> existingByName = productRepository.findByName(product.getName());
                if (existingByName.isPresent()) {
                    System.err.println("ERROR: El nombre ya existe: " + existingByName.get().getProductId());
                    return Optional.empty();
                }
            }

            System.out.println("Guardando producto en repositorio...");
            Optional<Product> saved = productRepository.save(product);

            if (saved.isPresent()) {
                System.out.println("Producto guardado exitosamente con ID: " + saved.get().getProductId());
            } else {
                System.err.println("ERROR: El repositorio retornó Optional.empty()");
            }

            return saved;
        } catch (Exception e) {
            System.err.println("EXCEPCIÓN en ProductService.save(): " + e.getMessage());
            e.printStackTrace();
            return Optional.empty();
        }
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