package com.profac.app.service.dto;

import com.profac.app.domain.enumeration.ProductStatus;
import jakarta.persistence.Lob;
import jakarta.validation.constraints.NotNull;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Objects;

/**
 * A DTO for the {@link com.profac.app.domain.Product} entity.
 */
@SuppressWarnings("common-java:DuplicatedBlocks")
public class ProductResponseDTO implements Serializable {

    private Long id;

    @NotNull(message = "must not be null")
    private Integer productNumber;

    @NotNull(message = "must not be null")
    private String name;

    @NotNull(message = "must not be null")
    private BigDecimal amount;

    private Integer quantity;

    @Lob
    private String description;

    private ProductStatus status;

    private CategoryDTO category;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Integer getProductNumber() {
        return productNumber;
    }

    public void setProductNumber(Integer productNumber) {
        this.productNumber = productNumber;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public ProductStatus getStatus() {
        return status;
    }

    public void setStatus(ProductStatus status) {
        this.status = status;
    }

    public CategoryDTO getCategory() {
        return category;
    }

    public void setCategory(CategoryDTO category) {
        this.category = category;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof ProductResponseDTO)) {
            return false;
        }

        ProductResponseDTO productDTO = (ProductResponseDTO) o;
        if (this.id == null) {
            return false;
        }
        return Objects.equals(this.id, productDTO.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.id);
    }

    public Integer getQuantity() {
        return quantity;
    }

    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }

    @Override
    public String toString() {
        return "ProductResponseDTO{" +
            "id=" + id +
            ", productNumber=" + productNumber +
            ", name='" + name + '\'' +
            ", amount=" + amount +
            ", quantity=" + quantity +
            ", description='" + description + '\'' +
            ", status=" + status +
            ", category=" + category +
            '}';
    }
}
