package com.profac.app.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.profac.app.domain.enumeration.InvoiceStatus;
import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

/**
 * A Invoice.
 */
@Table("invoice")
@SuppressWarnings("common-java:DuplicatedBlocks")
public class Invoice extends AbstractAuditingEntity<Long> implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @Column("id")
    private Long id;

    @Column("invoice_number")
    private Long invoiceNumber;

    @Column("customer")
    private String customer;

    @Column("invoice_date")
    private String invoiceDate;

    @Column("quantity")
    private Integer quantity;

    @Column("status")
    private InvoiceStatus status;

    @Transient
    @JsonIgnoreProperties(value = { "stocks", "images", "category", "invoices" }, allowSetters = true)
    private Set<Product> products = new HashSet<>();

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public Invoice id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getInvoiceNumber() {
        return this.invoiceNumber;
    }

    public Invoice invoiceNumber(Long invoiceNumber) {
        this.setInvoiceNumber(invoiceNumber);
        return this;
    }

    public void setInvoiceNumber(Long invoiceNumber) {
        this.invoiceNumber = invoiceNumber;
    }

    public String getCustomer() {
        return this.customer;
    }

    public Invoice customer(String customer) {
        this.setCustomer(customer);
        return this;
    }

    public void setCustomer(String customer) {
        this.customer = customer;
    }

    public String getInvoiceDate() {
        return this.invoiceDate;
    }

    public Invoice invoiceDate(String invoiceDate) {
        this.setInvoiceDate(invoiceDate);
        return this;
    }

    public void setInvoiceDate(String invoiceDate) {
        this.invoiceDate = invoiceDate;
    }

    public Integer getQuantity() {
        return this.quantity;
    }

    public Invoice quantity(Integer quantity) {
        this.setQuantity(quantity);
        return this;
    }

    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }

    public InvoiceStatus getStatus() {
        return this.status;
    }

    public Invoice status(InvoiceStatus status) {
        this.setStatus(status);
        return this;
    }

    public void setStatus(InvoiceStatus status) {
        this.status = status;
    }

    public Set<Product> getProducts() {
        return this.products;
    }

    public void setProducts(Set<Product> products) {
        this.products = products;
    }

    public Invoice products(Set<Product> products) {
        this.setProducts(products);
        return this;
    }

    public Invoice addProducts(Product product) {
        this.products.add(product);
        return this;
    }

    public Invoice removeProducts(Product product) {
        this.products.remove(product);
        return this;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Invoice)) {
            return false;
        }
        return getId() != null && getId().equals(((Invoice) o).getId());
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "Invoice{" +
            "id=" + getId() +
            ", invoiceNumber=" + getInvoiceNumber() +
            ", customer='" + getCustomer() + "'" +
            ", invoiceDate='" + getInvoiceDate() + "'" +
            ", quantity=" + getQuantity() +
            ", status='" + getStatus() + "'" +
            "}";
    }
}
