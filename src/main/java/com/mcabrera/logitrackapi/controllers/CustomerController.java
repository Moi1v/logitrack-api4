package com.mcabrera.logitrackapi.controllers;

import com.mcabrera.logitrackapi.models.Customer;
import com.mcabrera.logitrackapi.services.CustomerService;
import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import java.util.List;
import java.util.Optional;

@Path("/customers")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class CustomerController {

    @Inject
    private CustomerService customerService;

    @GET
    public Response getAllCustomers() {
        List<Customer> customers = customerService.getAll();
        return Response.ok(customers).build();
    }

    @GET
    @Path("/active")
    public Response getActiveCustomers() {
        List<Customer> customers = customerService.getActiveCustomers();
        return Response.ok(customers).build();
    }

    @GET
    @Path("/tax-id/{taxId}")
    public Response getCustomerByTaxId(@PathParam("taxId") String taxId) {
        Optional<Customer> customer = customerService.findByTaxId(taxId);
        if (customer.isEmpty()) {
            return Response.status(Response.Status.NOT_FOUND)
                    .entity("Cliente no encontrado")
                    .build();
        }
        return Response.ok(customer.get()).build();
    }

    @GET
    @Path("/{id}")
    public Response getCustomerById(@PathParam("id") Long id) {
        Optional<Customer> customer = customerService.findById(id);
        if (customer.isEmpty()) {
            return Response.status(Response.Status.NOT_FOUND)
                    .entity("Cliente no encontrado")
                    .build();
        }
        return Response.ok(customer.get()).build();
    }

    @POST
    public Response createCustomer(Customer customer) {
        // Validaciones
        if (customer.getFullName() == null || customer.getFullName().isBlank()) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity("El nombre completo es requerido")
                    .build();
        }

        if (customer.getEmail() == null || customer.getEmail().isBlank()) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity("El email es requerido")
                    .build();
        }

        if (customer.getActive() == null) {
            customer.setActive(true);
        }

        Optional<Customer> savedCustomer = customerService.save(customer);
        if (savedCustomer.isEmpty()) {
            return Response.status(Response.Status.CONFLICT)
                    .entity("El NIT o email ya existe")
                    .build();
        }

        return Response.status(Response.Status.CREATED)
                .entity(savedCustomer.get())
                .build();
    }

    @PUT
    @Path("/{id}")
    public Response updateCustomer(@PathParam("id") Long id, Customer customer) {
        Optional<Customer> existingCustomer = customerService.findById(id);
        if (existingCustomer.isEmpty()) {
            return Response.status(Response.Status.NOT_FOUND)
                    .entity("Cliente no encontrado")
                    .build();
        }

        Customer customerToUpdate = existingCustomer.get();
        customerToUpdate.setFullName(customer.getFullName());
        customerToUpdate.setTaxId(customer.getTaxId());
        customerToUpdate.setEmail(customer.getEmail());
        customerToUpdate.setAddress(customer.getAddress());
        customerToUpdate.setActive(customer.getActive());

        Optional<Customer> updatedCustomer = customerService.save(customerToUpdate);
        if (updatedCustomer.isEmpty()) {
            return Response.status(Response.Status.CONFLICT)
                    .entity("El NIT o email ya existe")
                    .build();
        }

        return Response.ok(updatedCustomer.get()).build();
    }

    @PATCH
    @Path("/{id}/deactivate")
    public Response deactivateCustomer(@PathParam("id") Long id) {
        Optional<Customer> customer = customerService.findById(id);
        if (customer.isEmpty()) {
            return Response.status(Response.Status.NOT_FOUND)
                    .entity("Cliente no encontrado")
                    .build();
        }

        customerService.deactivateCustomer(id);
        return Response.ok().entity("Cliente desactivado").build();
    }

    @PATCH
    @Path("/{id}/activate")
    public Response activateCustomer(@PathParam("id") Long id) {
        Optional<Customer> customer = customerService.findById(id);
        if (customer.isEmpty()) {
            return Response.status(Response.Status.NOT_FOUND)
                    .entity("Cliente no encontrado")
                    .build();
        }

        customerService.activateCustomer(id);
        return Response.ok().entity("Cliente activado").build();
    }

    @DELETE
    @Path("/{id}")
    public Response deleteCustomer(@PathParam("id") Long id) {
        Optional<Customer> customer = customerService.findById(id);
        if (customer.isEmpty()) {
            return Response.status(Response.Status.NOT_FOUND)
                    .entity("Cliente no encontrado")
                    .build();
        }

        customerService.delete(customer.get());
        return Response.status(Response.Status.NO_CONTENT).build();
    }
}