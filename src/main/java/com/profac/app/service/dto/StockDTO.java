package com.profac.app.service.dto;

import com.profac.app.domain.AbstractAuditingEntity;
import com.profac.app.domain.enumeration.StockStatus;
import jakarta.validation.constraints.*;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Objects;

/**
 * A DTO for the {@link com.profac.app.domain.Stock} entity.
 */
@SuppressWarnings("common-java:DuplicatedBlocks")
public class StockDTO extends AbstractAuditingEntity<Long> implements Serializable {

    private Long id;

    private BigDecimal totalAmount;
    private BigDecimal totalAmountSold;
    private Integer initialQuantity;
    private Integer remainingQuantity;
    private StockStatus status;
    private CompanyDTO company;
    private ProductDTO product;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public BigDecimal getTotalAmount() {
        return totalAmount;
    }

    public void setTotalAmount(BigDecimal totalAmount) {
        this.totalAmount = totalAmount;
    }

    public BigDecimal getTotalAmountSold() {
        return totalAmountSold;
    }

    public void setTotalAmountSold(BigDecimal totalAmountSold) {
        this.totalAmountSold = totalAmountSold;
    }

    public Integer getInitialQuantity() {
        return initialQuantity;
    }

    public void setInitialQuantity(Integer initialQuantity) {
        this.initialQuantity = initialQuantity;
    }

    public Integer getRemainingQuantity() {
        return remainingQuantity;
    }

    public void setRemainingQuantity(Integer remainingQuantity) {
        this.remainingQuantity = remainingQuantity;
    }

    public StockStatus getStatus() {
        return status;
    }

    public void setStatus(StockStatus status) {
        this.status = status;
    }

    public CompanyDTO getCompany() {
        return company;
    }

    public void setCompany(CompanyDTO company) {
        this.company = company;
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
        if (!(o instanceof StockDTO)) {
            return false;
        }

        StockDTO stockDTO = (StockDTO) o;
        if (this.id == null) {
            return false;
        }
        return Objects.equals(this.id, stockDTO.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.id);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "StockDTO{" +
            "id=" + getId() +
            ", totalAmount=" + getTotalAmount() +
            ", totalAmountSold=" + getTotalAmountSold() +
            ", initialQuantity=" + getInitialQuantity() +
            ", remainingQuantity=" + getRemainingQuantity() +
            ", status='" + getStatus() + "'" +
            ", company=" + getCompany() +
            ", product=" + getProduct() +
            "}";
    }
}
