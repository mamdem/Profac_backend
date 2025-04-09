package com.profac.app.service.dto;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.profac.app.domain.enumeration.InvoiceStatus;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.HashSet;
import java.util.Set;

public class InvoiceResponseDTO {
    private Long id;
    private Instant createAt;
    private Long invoiceNumber;
    private String customer;
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private BigDecimal amount;
    private InvoiceStatus status;
    private Set<ProductResponseDTO> products = new HashSet<>();

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

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public InvoiceStatus getStatus() {
        return status;
    }

    public void setStatus(InvoiceStatus status) {
        this.status = status;
    }

    public Set<ProductResponseDTO> getProducts() {
        return products;
    }

    public void setProducts(Set<ProductResponseDTO> products) {
        this.products = products;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Instant getCreateAt() {
        return createAt;
    }

    public void setCreateAt(Instant createAt) {
        this.createAt = createAt;
    }

    @Override
    public String toString() {
        return "InvoiceResponseDTO{" +
            "id=" + id +
            ", createAt=" + createAt +
            ", invoiceNumber=" + invoiceNumber +
            ", customer='" + customer + '\'' +
            ", amount=" + amount +
            ", status=" + status +
            ", products=" + products +
            '}';
    }
}
