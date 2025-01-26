package com.profac.app.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.profac.app.domain.enumeration.ProductStatus;
import jakarta.validation.constraints.*;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.HashSet;
import java.util.Set;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

/**
 * A Product.
 */
@Table("product")
@SuppressWarnings("common-java:DuplicatedBlocks")
public class Product extends AbstractAuditingEntity<Long> implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @Column("id")
    private Long id;

    @NotNull(message = "must not be null")
    @Column("product_number")
    private Integer productNumber;

    @NotNull(message = "must not be null")
    @Column("name")
    private String name;

    @NotNull(message = "must not be null")
    @Column("amount")
    private BigDecimal amount;

    @Column("description")
    private String description;

    @Column("status")
    private ProductStatus status;

    @Transient
    @JsonIgnoreProperties(value = { "company", "product" }, allowSetters = true)
    private Set<Stock> stocks = new HashSet<>();

    @Transient
    @JsonIgnoreProperties(value = { "appUser", "product" }, allowSetters = true)
    private Set<Image> images = new HashSet<>();

    @Transient
    private Category category;

    @Transient
    @JsonIgnoreProperties(value = { "invoice", "product" }, allowSetters = true)
    private Set<InvoiceProduct> invoiceProducts = new HashSet<>();

    @Column("category_id")
    private Long categoryId;

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public Product id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Integer getProductNumber() {
        return this.productNumber;
    }

    public Product productNumber(Integer productNumber) {
        this.setProductNumber(productNumber);
        return this;
    }

    public void setProductNumber(Integer productNumber) {
        this.productNumber = productNumber;
    }

    public String getName() {
        return this.name;
    }

    public Product name(String name) {
        this.setName(name);
        return this;
    }

    public void setName(String name) {
        this.name = name;
    }

    public BigDecimal getAmount() {
        return this.amount;
    }

    public Product amount(BigDecimal amount) {
        this.setAmount(amount);
        return this;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount != null ? amount.stripTrailingZeros() : null;
    }

    public String getDescription() {
        return this.description;
    }

    public Product description(String description) {
        this.setDescription(description);
        return this;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public ProductStatus getStatus() {
        return this.status;
    }

    public Product status(ProductStatus status) {
        this.setStatus(status);
        return this;
    }

    public void setStatus(ProductStatus status) {
        this.status = status;
    }

    public Set<Stock> getStocks() {
        return this.stocks;
    }

    public void setStocks(Set<Stock> stocks) {
        if (this.stocks != null) {
            this.stocks.forEach(i -> i.setProduct(null));
        }
        if (stocks != null) {
            stocks.forEach(i -> i.setProduct(this));
        }
        this.stocks = stocks;
    }

    public Product stocks(Set<Stock> stocks) {
        this.setStocks(stocks);
        return this;
    }

    public Product addStocks(Stock stock) {
        this.stocks.add(stock);
        stock.setProduct(this);
        return this;
    }

    public Product removeStocks(Stock stock) {
        this.stocks.remove(stock);
        stock.setProduct(null);
        return this;
    }

    public Set<Image> getImages() {
        return this.images;
    }

    public void setImages(Set<Image> images) {
        if (this.images != null) {
            this.images.forEach(i -> i.setProduct(null));
        }
        if (images != null) {
            images.forEach(i -> i.setProduct(this));
        }
        this.images = images;
    }

    public Product images(Set<Image> images) {
        this.setImages(images);
        return this;
    }

    public Product addImages(Image image) {
        this.images.add(image);
        image.setProduct(this);
        return this;
    }

    public Product removeImages(Image image) {
        this.images.remove(image);
        image.setProduct(null);
        return this;
    }

    public Category getCategory() {
        return this.category;
    }

    public void setCategory(Category category) {
        this.category = category;
        this.categoryId = category != null ? category.getId() : null;
    }

    public Product category(Category category) {
        this.setCategory(category);
        return this;
    }

    public Set<InvoiceProduct> getInvoiceProducts() {
        return this.invoiceProducts;
    }

    public void setInvoiceProducts(Set<InvoiceProduct> invoiceProducts) {
        if (this.invoiceProducts != null) {
            this.invoiceProducts.forEach(i -> i.setProduct(null));
        }
        if (invoiceProducts != null) {
            invoiceProducts.forEach(i -> i.setProduct(this));
        }
        this.invoiceProducts = invoiceProducts;
    }

    public Product invoiceProducts(Set<InvoiceProduct> invoiceProducts) {
        this.setInvoiceProducts(invoiceProducts);
        return this;
    }

    public Product addInvoiceProducts(InvoiceProduct invoiceProduct) {
        this.invoiceProducts.add(invoiceProduct);
        invoiceProduct.setProduct(this);
        return this;
    }

    public Product removeInvoiceProducts(InvoiceProduct invoiceProduct) {
        this.invoiceProducts.remove(invoiceProduct);
        invoiceProduct.setProduct(null);
        return this;
    }

    public Long getCategoryId() {
        return this.categoryId;
    }

    public void setCategoryId(Long category) {
        this.categoryId = category;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Product)) {
            return false;
        }
        return getId() != null && getId().equals(((Product) o).getId());
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "Product{" +
            "id=" + getId() +
            ", productNumber=" + getProductNumber() +
            ", name='" + getName() + "'" +
            ", amount=" + getAmount() +
            ", description='" + getDescription() + "'" +
            ", status='" + getStatus() + "'" +
            "}";
    }
}
