package com.profac.app.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.validation.constraints.*;
import java.io.Serializable;
import java.math.BigDecimal;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

/**
 * A InvoiceProduct.
 */
@Table("invoice_product")
@SuppressWarnings("common-java:DuplicatedBlocks")
public class InvoiceProduct extends AbstractAuditingEntity<Long>  implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @Column("id")
    private Long id;

    @NotNull(message = "must not be null")
    @Column("quantity")
    private Integer quantity;

    @Column("total_amount")
    private BigDecimal totalAmount;

    @Transient
    @JsonIgnoreProperties(value = { "invoiceProducts" }, allowSetters = true)
    private Invoice invoice;

    @Transient
    @JsonIgnoreProperties(value = { "stocks", "images", "category", "company", "invoiceProducts" }, allowSetters = true)
    private Product product;

    @Column("invoice_id")
    private Long invoiceId;

    @Column("product_id")
    private Long productId;

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public InvoiceProduct id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Integer getQuantity() {
        return this.quantity;
    }

    public InvoiceProduct quantity(Integer quantity) {
        this.setQuantity(quantity);
        return this;
    }

    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }

    public BigDecimal getTotalAmount() {
        return this.totalAmount;
    }

    public InvoiceProduct totalAmount(BigDecimal totalAmount) {
        this.setTotalAmount(totalAmount);
        return this;
    }

    public void setTotalAmount(BigDecimal totalAmount) {
        this.totalAmount = totalAmount != null ? totalAmount.stripTrailingZeros() : null;
    }

    public Invoice getInvoice() {
        return this.invoice;
    }

    public void setInvoice(Invoice invoice) {
        this.invoice = invoice;
        this.invoiceId = invoice != null ? invoice.getId() : null;
    }

    public InvoiceProduct invoice(Invoice invoice) {
        this.setInvoice(invoice);
        return this;
    }

    public Product getProduct() {
        return this.product;
    }

    public void setProduct(Product product) {
        this.product = product;
        this.productId = product != null ? product.getId() : null;
    }

    public InvoiceProduct product(Product product) {
        this.setProduct(product);
        return this;
    }

    public Long getInvoiceId() {
        return this.invoiceId;
    }

    public void setInvoiceId(Long invoice) {
        this.invoiceId = invoice;
    }

    public Long getProductId() {
        return this.productId;
    }

    public void setProductId(Long product) {
        this.productId = product;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof InvoiceProduct)) {
            return false;
        }
        return getId() != null && getId().equals(((InvoiceProduct) o).getId());
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "InvoiceProduct{" +
            "id=" + getId() +
            ", quantity=" + getQuantity() +
            ", totalAmount=" + getTotalAmount() +
            "}";
    }
}
