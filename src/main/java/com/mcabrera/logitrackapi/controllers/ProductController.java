package com.mcabrera.logitrackapi.controllers;

import com.mcabrera.logitrackapi.models.Product;
import com.mcabrera.logitrackapi.services.ProductService;
import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@Path("/products")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class ProductController {

    @Inject
    private ProductService productService;

    @GET
    public Response getAllProducts() {
        List<Product> products = productService.getAll();
        return Response.ok(products).build();
    }

    @GET
    @Path("/active")
    public Response getActiveProducts() {
        List<Product> products = productService.getActiveProducts();
        return Response.ok(products).build();
    }

    @GET
    @Path("/category/{category}")
    public Response getProductsByCategory(@PathParam("category") String category) {
        List<Product> products = productService.getProductsByCategory(category);
        return Response.ok(products).build();
    }

    @GET
    @Path("/{id}")
    public Response getProductById(@PathParam("id") Long id) {
        Optional<Product> product = productService.findById(id);
        if (product.isEmpty()) {
            return Response.status(Response.Status.NOT_FOUND)
                    .entity("Producto no encontrado")
                    .build();
        }
        return Response.ok(product.get()).build();
    }

    @POST
    public Response createProduct(Product product) {
        // Validaciones
        if (product.getName() == null || product.getName().isBlank()) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity("El nombre del producto es requerido")
                    .build();
        }

        if (product.getPrice() == null || product.getPrice().compareTo(BigDecimal.ZERO) <= 0) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity("El precio debe ser mayor a cero")
                    .build();
        }

        if (product.getActive() == null) {
            product.setActive(true);
        }

        Optional<Product> savedProduct = productService.save(product);
        if (savedProduct.isEmpty()) {
            return Response.status(Response.Status.CONFLICT)
                    .entity("Ya existe un producto con ese nombre")
                    .build();
        }

        return Response.status(Response.Status.CREATED)
                .entity(savedProduct.get())
                .build();
    }

    @PUT
    @Path("/{id}")
    public Response updateProduct(@PathParam("id") Long id, Product product) {
        Optional<Product> existingProduct = productService.findById(id);
        if (existingProduct.isEmpty()) {
            return Response.status(Response.Status.NOT_FOUND)
                    .entity("Producto no encontrado")
                    .build();
        }

        Product productToUpdate = existingProduct.get();
        productToUpdate.setName(product.getName());
        productToUpdate.setDescription(product.getDescription());
        productToUpdate.setPrice(product.getPrice());
        productToUpdate.setCategory(product.getCategory());
        productToUpdate.setActive(product.getActive());

        Optional<Product> updatedProduct = productService.save(productToUpdate);
        if (updatedProduct.isEmpty()) {
            return Response.status(Response.Status.CONFLICT)
                    .entity("Ya existe un producto con ese nombre")
                    .build();
        }

        return Response.ok(updatedProduct.get()).build();
    }

    @PATCH
    @Path("/{id}/deactivate")
    public Response deactivateProduct(@PathParam("id") Long id) {
        Optional<Product> product = productService.findById(id);
        if (product.isEmpty()) {
            return Response.status(Response.Status.NOT_FOUND)
                    .entity("Producto no encontrado")
                    .build();
        }

        productService.deactivateProduct(id);
        return Response.ok().entity("Producto desactivado").build();
    }

    @PATCH
    @Path("/{id}/activate")
    public Response activateProduct(@PathParam("id") Long id) {
        Optional<Product> product = productService.findById(id);
        if (product.isEmpty()) {
            return Response.status(Response.Status.NOT_FOUND)
                    .entity("Producto no encontrado")
                    .build();
        }

        productService.activateProduct(id);
        return Response.ok().entity("Producto activado").build();
    }

    @DELETE
    @Path("/{id}")
    public Response deleteProduct(@PathParam("id") Long id) {
        Optional<Product> product = productService.findById(id);
        if (product.isEmpty()) {
            return Response.status(Response.Status.NOT_FOUND)
                    .entity("Producto no encontrado")
                    .build();
        }

        productService.delete(product.get());
        return Response.status(Response.Status.NO_CONTENT).build();
    }
}