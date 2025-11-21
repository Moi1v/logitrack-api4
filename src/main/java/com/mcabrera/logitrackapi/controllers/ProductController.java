package com.mcabrera.logitrackapi.controllers;

import com.mcabrera.logitrackapi.dtos.ProductStatsDto;
import com.mcabrera.logitrackapi.models.Product;
import com.mcabrera.logitrackapi.repositories.ProductRepository;
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

    @Inject
    private ProductRepository productRepository;

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
    @Path("/top-selling")
    public Response getTopSellingProducts(@QueryParam("limit") @DefaultValue("10") int limit) {
        if (limit <= 0 || limit > 100) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity(new ErrorResponse("El límite debe estar entre 1 y 100"))
                    .build();
        }

        List<ProductStatsDto> topProducts = productRepository.findTopSellingProducts(limit);
        return Response.ok(topProducts).build();
    }

    @GET
    @Path("/top-selling/category/{category}")
    public Response getTopSellingProductsByCategory(
            @PathParam("category") String category,
            @QueryParam("limit") @DefaultValue("10") int limit) {

        if (limit <= 0 || limit > 100) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity(new ErrorResponse("El límite debe estar entre 1 y 100"))
                    .build();
        }

        List<ProductStatsDto> topProducts = productRepository.findTopSellingProductsByCategory(category, limit);
        return Response.ok(topProducts).build();
    }

    @GET
    @Path("/{id}/stats")
    public Response getProductStats(@PathParam("id") Long id) {
        Optional<ProductStatsDto> stats = productRepository.getProductSalesStats(id);
        if (stats.isEmpty()) {
            return Response.status(Response.Status.NOT_FOUND)
                    .entity(new ErrorResponse("Producto no encontrado"))
                    .build();
        }
        return Response.ok(stats.get()).build();
    }

    @GET
    @Path("/{id}")
    public Response getProductById(@PathParam("id") Long id) {
        Optional<Product> product = productService.findById(id);
        if (product.isEmpty()) {
            return Response.status(Response.Status.NOT_FOUND)
                    .entity(new ErrorResponse("Producto no encontrado"))
                    .build();
        }
        return Response.ok(product.get()).build();
    }

    @POST
    public Response createProduct(Product product) {
        // Validaciones
        if (product.getName() == null || product.getName().isBlank()) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity(new ErrorResponse("El nombre del producto es requerido"))
                    .build();
        }

        if (product.getPrice() == null || product.getPrice().compareTo(BigDecimal.ZERO) <= 0) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity(new ErrorResponse("El precio debe ser mayor a cero"))
                    .build();
        }

        // Verificar si ya existe un producto con ese nombre
        Optional<Product> existingProduct = productService.findByName(product.getName());
        if (existingProduct.isPresent()) {
            return Response.status(Response.Status.CONFLICT)
                    .entity(new ErrorResponse("Ya existe un producto con el nombre: " + product.getName()))
                    .build();
        }

        if (product.getActive() == null) {
            product.setActive(true);
        }

        // Asegurarse de que el ID sea null para creación
        product.setProductId(null);

        Optional<Product> savedProduct = productService.save(product);
        if (savedProduct.isEmpty()) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity(new ErrorResponse("Error al guardar el producto"))
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
                    .entity(new ErrorResponse("Producto no encontrado"))
                    .build();
        }

        // Verificar si el nuevo nombre ya existe en otro producto
        if (product.getName() != null && !product.getName().isBlank()) {
            Optional<Product> productWithSameName = productService.findByName(product.getName());
            if (productWithSameName.isPresent() &&
                    !productWithSameName.get().getProductId().equals(id)) {
                return Response.status(Response.Status.CONFLICT)
                        .entity(new ErrorResponse("Ya existe otro producto con el nombre: " + product.getName()))
                        .build();
            }
        }

        Product productToUpdate = existingProduct.get();
        productToUpdate.setName(product.getName());
        productToUpdate.setDescription(product.getDescription());
        productToUpdate.setPrice(product.getPrice());
        productToUpdate.setCategory(product.getCategory());
        if (product.getActive() != null) {
            productToUpdate.setActive(product.getActive());
        }

        Optional<Product> updatedProduct = productService.save(productToUpdate);
        if (updatedProduct.isEmpty()) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity(new ErrorResponse("Error al actualizar el producto"))
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
                    .entity(new ErrorResponse("Producto no encontrado"))
                    .build();
        }

        productService.deactivateProduct(id);
        return Response.ok().entity(new SuccessResponse("Producto desactivado")).build();
    }

    @PATCH
    @Path("/{id}/activate")
    public Response activateProduct(@PathParam("id") Long id) {
        Optional<Product> product = productService.findById(id);
        if (product.isEmpty()) {
            return Response.status(Response.Status.NOT_FOUND)
                    .entity(new ErrorResponse("Producto no encontrado"))
                    .build();
        }

        productService.activateProduct(id);
        return Response.ok().entity(new SuccessResponse("Producto activado")).build();
    }

    @DELETE
    @Path("/{id}")
    public Response deleteProduct(@PathParam("id") Long id) {
        Optional<Product> product = productService.findById(id);
        if (product.isEmpty()) {
            return Response.status(Response.Status.NOT_FOUND)
                    .entity(new ErrorResponse("Producto no encontrado"))
                    .build();
        }

        productService.delete(product.get());
        return Response.status(Response.Status.NO_CONTENT).build();
    }

    // Clases internas para respuestas
    public static class ErrorResponse {
        private String error;

        public ErrorResponse(String error) {
            this.error = error;
        }

        public String getError() {
            return error;
        }

        public void setError(String error) {
            this.error = error;
        }
    }

    public static class SuccessResponse {
        private String message;

        public SuccessResponse(String message) {
            this.message = message;
        }

        public String getMessage() {
            return message;
        }

        public void setMessage(String message) {
            this.message = message;
        }
    }
}