package com.profac.app.service.dto;
import com.profac.app.domain.enumeration.InvoiceStatus;
import java.math.BigDecimal;
import java.util.HashSet;
import java.util.Set;

public class InvoiceResponseDTO {
    private Long invoiceNumber;
    private String customer;
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

    @Override
    public String toString() {
        return "InvoiceResponseDTO{" +
            "invoiceNumber=" + invoiceNumber +
            ", customer='" + customer + '\'' +
            ", amount=" + amount +
            ", status=" + status +
            ", products=" + products +
            '}';
    }
}
