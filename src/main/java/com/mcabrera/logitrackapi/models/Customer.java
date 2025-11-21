package com.mcabrera.logitrackapi.models;

import jakarta.persistence.*;
import org.hibernate.annotations.CollectionId;

@Entity(name = "Customers")
public class Customer {

    @Id
    @GeneratedValue
    @Column(name = "customerId")
    private Long customerId;

    @Column(name = "fullName")
    private String fullName;

    @Column(name = "taxId")
    private String taxId;
    @Column(name = "email")
    private String email;
    private String address;
    private Boolean active;

    public Long getCustomerId() {
        return customerId;
    }

    public void setCustomerId(Long customerId) {
        this.customerId = customerId;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getTaxId() {
        return taxId;
    }

    public void setTaxId(String taxId) {
        this.taxId = taxId;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public Boolean getActive() {
        return active;
    }

    public void setActive(Boolean active) {
        this.active = active;
    }

    @Override
    public String toString() {
        final StringBuffer sb = new StringBuffer("Customer{");
        sb.append("customerId=").append(customerId);
        sb.append(", fullName='").append(fullName).append('\'');
        sb.append(", taxId='").append(taxId).append('\'');
        sb.append(", email='").append(email).append('\'');
        sb.append(", address='").append(address).append('\'');
        sb.append(", active=").append(active);
        sb.append('}');
        return sb.toString();
    }
}
