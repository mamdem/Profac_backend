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

    @Column("status")
    private InvoiceStatus status;

    @Transient
    @JsonIgnoreProperties(value = { "appUsers", "products", "invoices" }, allowSetters = true)
    private Company company;

    @Transient
    @JsonIgnoreProperties(value = { "invoice", "product" }, allowSetters = true)
    private Set<InvoiceProduct> invoiceProducts = new HashSet<>();

    @Column("company_id")
    private Long companyId;

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

    public Company getCompany() {
        return this.company;
    }

    public void setCompany(Company company) {
        this.company = company;
        this.companyId = company != null ? company.getId() : null;
    }

    public Invoice company(Company company) {
        this.setCompany(company);
        return this;
    }

    public Set<InvoiceProduct> getInvoiceProducts() {
        return this.invoiceProducts;
    }

    public void setInvoiceProducts(Set<InvoiceProduct> invoiceProducts) {
        if (this.invoiceProducts != null) {
            this.invoiceProducts.forEach(i -> i.setInvoice(null));
        }
        if (invoiceProducts != null) {
            invoiceProducts.forEach(i -> i.setInvoice(this));
        }
        this.invoiceProducts = invoiceProducts;
    }

    public Invoice invoiceProducts(Set<InvoiceProduct> invoiceProducts) {
        this.setInvoiceProducts(invoiceProducts);
        return this;
    }

    public Invoice addInvoiceProducts(InvoiceProduct invoiceProduct) {
        this.invoiceProducts.add(invoiceProduct);
        invoiceProduct.setInvoice(this);
        return this;
    }

    public Invoice removeInvoiceProducts(InvoiceProduct invoiceProduct) {
        this.invoiceProducts.remove(invoiceProduct);
        invoiceProduct.setInvoice(null);
        return this;
    }

    public Long getCompanyId() {
        return this.companyId;
    }

    public void setCompanyId(Long company) {
        this.companyId = company;
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
            ", status='" + getStatus() + "'" +
            "}";
    }
}
