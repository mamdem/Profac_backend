package com.profac.app.service.dto;

import com.profac.app.domain.AbstractAuditingEntity;
import jakarta.validation.constraints.*;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Objects;

/**
 * A DTO for the {@link com.profac.app.domain.InvoiceProduct} entity.
 */
@SuppressWarnings("common-java:DuplicatedBlocks")
public class InvoiceProductDTO implements Serializable {

    private Long id;

    @NotNull(message = "must not be null")
    private Integer quantity;

    private BigDecimal totalAmount;

    private InvoiceDTO invoice;

    private ProductDTO product;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Integer getQuantity() {
        return quantity;
    }

    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }

    public BigDecimal getTotalAmount() {
        return totalAmount;
    }

    public void setTotalAmount(BigDecimal totalAmount) {
        this.totalAmount = totalAmount;
    }

    public InvoiceDTO getInvoice() {
        return invoice;
    }

    public void setInvoice(InvoiceDTO invoice) {
        this.invoice = invoice;
    }

    public ProductDTO getProduct() {
        return product;
    }

    public void setProduct(ProductDTO product) {
        this.product = product;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof InvoiceProductDTO)) {
            return false;
        }

        InvoiceProductDTO invoiceProductDTO = (InvoiceProductDTO) o;
        if (this.id == null) {
            return false;
        }
        return Objects.equals(this.id, invoiceProductDTO.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.id);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "InvoiceProductDTO{" +
            "id=" + getId() +
            ", quantity=" + getQuantity() +
            ", totalAmount=" + getTotalAmount() +
            ", invoice=" + getInvoice() +
            ", product=" + getProduct() +
            "}";
    }
}
