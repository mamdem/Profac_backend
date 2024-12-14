package com.profac.app.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.profac.app.domain.enumeration.StockStatus;
import jakarta.validation.constraints.*;
import java.io.Serializable;
import java.math.BigDecimal;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

/**
 * A Stock.
 */
@Table("stock")
@SuppressWarnings("common-java:DuplicatedBlocks")
public class Stock extends AbstractAuditingEntity<Long> implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @Column("id")
    private Long id;

    @NotNull(message = "must not be null")
    @jakarta.persistence.Column(name = "total_amount", precision = 21, scale = 2)
    private BigDecimal totalAmount;

    @NotNull(message = "must not be null")
    @jakarta.persistence.Column(name = "total_amount_sold", precision = 21, scale = 2)
    private BigDecimal totalAmountSold;

    @NotNull(message = "must not be null")
    @Column("initial_quantity")
    private Integer initialQuantity;

    @NotNull(message = "must not be null")
    @Column("remaining_quantity")
    private Integer remainingQuantity;

    @Column("status")
    private StockStatus status;

    @Transient
    @JsonIgnoreProperties(value = { "stocks", "images", "category", "invoices" }, allowSetters = true)
    private Product product;

    @Column("product_id")
    private Long productId;

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public Stock id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public BigDecimal getTotalAmount() {
        return this.totalAmount;
    }

    public Stock totalAmount(BigDecimal totalAmount) {
        this.setTotalAmount(totalAmount);
        return this;
    }

    public void setTotalAmount(BigDecimal totalAmount) {
        this.totalAmount = totalAmount;
    }

    public BigDecimal getTotalAmountSold() {
        return this.totalAmountSold;
    }

    public Stock totalAmountSold(BigDecimal totalAmountSold) {
        this.setTotalAmountSold(totalAmountSold);
        return this;
    }

    public void setTotalAmountSold(BigDecimal totalAmountSold) {
        this.totalAmountSold = totalAmountSold ;
    }

    public Integer getInitialQuantity() {
        return this.initialQuantity;
    }

    public Stock initialQuantity(Integer initialQuantity) {
        this.setInitialQuantity(initialQuantity);
        return this;
    }

    public void setInitialQuantity(Integer initialQuantity) {
        this.initialQuantity = initialQuantity;
    }

    public Integer getRemainingQuantity() {
        return this.remainingQuantity;
    }

    public Stock remainingQuantity(Integer remainingQuantity) {
        this.setRemainingQuantity(remainingQuantity);
        return this;
    }

    public void setRemainingQuantity(Integer remainingQuantity) {
        this.remainingQuantity = remainingQuantity;
    }

    public StockStatus getStatus() {
        return this.status;
    }

    public Stock status(StockStatus status) {
        this.setStatus(status);
        return this;
    }

    public void setStatus(StockStatus status) {
        this.status = status;
    }

    public Product getProduct() {
        return this.product;
    }

    public void setProduct(Product product) {
        this.product = product;
        this.productId = product != null ? product.getId() : null;
    }

    public Stock product(Product product) {
        this.setProduct(product);
        return this;
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
        if (!(o instanceof Stock)) {
            return false;
        }
        return getId() != null && getId().equals(((Stock) o).getId());
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "Stock{" +
            "id=" + getId() +
            ", totalAmount=" + getTotalAmount() +
            ", totalAmountSold=" + getTotalAmountSold() +
            ", initialQuantity=" + getInitialQuantity() +
            ", remainingQuantity=" + getRemainingQuantity() +
            ", status='" + getStatus() + "'" +
            "}";
    }
}
