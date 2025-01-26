package com.profac.app.service.dto;

import com.profac.app.domain.AbstractAuditingEntity;
import com.profac.app.domain.enumeration.InvoiceStatus;
import java.io.Serializable;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

/**
 * A DTO for the {@link com.profac.app.domain.Invoice} entity.
 */
@SuppressWarnings("common-java:DuplicatedBlocks")
public class InvoiceDTO extends AbstractAuditingEntity<Long> implements Serializable {
    private Long id;

    private Long invoiceNumber;

    private String customer;

    private String invoiceDate;

    private InvoiceStatus status;

    private CompanyDTO company;

    private Set<ExposedInvoiceProductDTO> products = new HashSet<>();

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getInvoiceNumber() {
        return invoiceNumber;
    }

    public void setInvoiceNumber(Long invoiceNumber) {
        this.invoiceNumber = invoiceNumber;
    }

    public String getCustomer() {
        return customer;
    }

    public void setCustomer(String customer) {
        this.customer = customer;
    }

    public String getInvoiceDate() {
        return invoiceDate;
    }

    public void setInvoiceDate(String invoiceDate) {
        this.invoiceDate = invoiceDate;
    }

    public InvoiceStatus getStatus() {
        return status;
    }

    public void setStatus(InvoiceStatus status) {
        this.status = status;
    }

    public CompanyDTO getCompany() {
        return company;
    }

    public void setCompany(CompanyDTO company) {
        this.company = company;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof InvoiceDTO)) {
            return false;
        }

        InvoiceDTO invoiceDTO = (InvoiceDTO) o;
        if (this.id == null) {
            return false;
        }
        return Objects.equals(this.id, invoiceDTO.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.id);
    }

    public Set<ExposedInvoiceProductDTO> getProducts() {
        return products;
    }

    public void setProducts(Set<ExposedInvoiceProductDTO> products) {
        this.products = products;
    }

    @Override
    public String toString() {
        return "InvoiceDTO{" +
            "id=" + id +
            ", invoiceNumber=" + invoiceNumber +
            ", customer='" + customer + '\'' +
            ", invoiceDate='" + invoiceDate + '\'' +
            ", status=" + status +
            ", company=" + company +
            ", products=" + products +
            '}';
    }
}
