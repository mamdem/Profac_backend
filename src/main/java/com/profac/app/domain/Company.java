package com.profac.app.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.profac.app.domain.enumeration.CompanyStatus;
import jakarta.validation.constraints.*;
import java.io.Serializable;
import java.time.Instant;
import java.util.HashSet;
import java.util.Set;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

/**
 * A Company.
 */
@Table("company")
@SuppressWarnings("common-java:DuplicatedBlocks")
public class Company extends AbstractAuditingEntity<Long> implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @Column("id")
    private Long id;

    @NotNull(message = "must not be null")
    @Column("name")
    private String name;

    @Column("valid_until")
    private Instant validUntil;

    @Column("status")
    private CompanyStatus status;

    @Column("password")
    private String password;

    @NotNull(message = "must not be null")
    @Column("phone_number")
    private String phoneNumber;

    @Transient
    @JsonIgnoreProperties(value = { "avatar", "company" }, allowSetters = true)
    private Set<AppUser> appUsers = new HashSet<>();

    @Transient
    @JsonIgnoreProperties(value = { "company", "invoiceProducts" }, allowSetters = true)
    private Set<Invoice> invoices = new HashSet<>();

    @Transient
    @JsonIgnoreProperties(value = { "company", "product" }, allowSetters = true)
    private Set<Stock> stocks = new HashSet<>();

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public Company id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return this.name;
    }

    public Company name(String name) {
        this.setName(name);
        return this;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Instant getValidUntil() {
        return this.validUntil;
    }

    public Company validUntil(Instant validUntil) {
        this.setValidUntil(validUntil);
        return this;
    }

    public void setValidUntil(Instant validUntil) {
        this.validUntil = validUntil;
    }

    public CompanyStatus getStatus() {
        return this.status;
    }

    public Company status(CompanyStatus status) {
        this.setStatus(status);
        return this;
    }

    public void setStatus(CompanyStatus status) {
        this.status = status;
    }

    public String getPassword() {
        return this.password;
    }

    public Company password(String password) {
        this.setPassword(password);
        return this;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getPhoneNumber() {
        return this.phoneNumber;
    }

    public Company phoneNumber(String phoneNumber) {
        this.setPhoneNumber(phoneNumber);
        return this;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public Set<AppUser> getAppUsers() {
        return this.appUsers;
    }

    public void setAppUsers(Set<AppUser> appUsers) {
        if (this.appUsers != null) {
            this.appUsers.forEach(i -> i.setCompany(null));
        }
        if (appUsers != null) {
            appUsers.forEach(i -> i.setCompany(this));
        }
        this.appUsers = appUsers;
    }

    public Company appUsers(Set<AppUser> appUsers) {
        this.setAppUsers(appUsers);
        return this;
    }

    public Company addAppUsers(AppUser appUser) {
        this.appUsers.add(appUser);
        appUser.setCompany(this);
        return this;
    }

    public Company removeAppUsers(AppUser appUser) {
        this.appUsers.remove(appUser);
        appUser.setCompany(null);
        return this;
    }

    public Set<Invoice> getInvoices() {
        return this.invoices;
    }

    public void setInvoices(Set<Invoice> invoices) {
        if (this.invoices != null) {
            this.invoices.forEach(i -> i.setCompany(null));
        }
        if (invoices != null) {
            invoices.forEach(i -> i.setCompany(this));
        }
        this.invoices = invoices;
    }

    public Company invoices(Set<Invoice> invoices) {
        this.setInvoices(invoices);
        return this;
    }

    public Company addInvoices(Invoice invoice) {
        this.invoices.add(invoice);
        invoice.setCompany(this);
        return this;
    }

    public Company removeInvoices(Invoice invoice) {
        this.invoices.remove(invoice);
        invoice.setCompany(null);
        return this;
    }

    public Set<Stock> getStocks() {
        return this.stocks;
    }

    public void setStocks(Set<Stock> stocks) {
        if (this.stocks != null) {
            this.stocks.forEach(i -> i.setCompany(null));
        }
        if (stocks != null) {
            stocks.forEach(i -> i.setCompany(this));
        }
        this.stocks = stocks;
    }

    public Company stocks(Set<Stock> stocks) {
        this.setStocks(stocks);
        return this;
    }

    public Company addStocks(Stock stock) {
        this.stocks.add(stock);
        stock.setCompany(this);
        return this;
    }

    public Company removeStocks(Stock stock) {
        this.stocks.remove(stock);
        stock.setCompany(null);
        return this;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Company)) {
            return false;
        }
        return getId() != null && getId().equals(((Company) o).getId());
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "Company{" +
            "id=" + getId() +
            ", name='" + getName() + "'" +
            ", validUntil='" + getValidUntil() + "'" +
            ", status='" + getStatus() + "'" +
            ", password='" + getPassword() + "'" +
            ", phoneNumber='" + getPhoneNumber() + "'" +
            "}";
    }
}
