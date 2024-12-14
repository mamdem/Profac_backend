package com.profac.app.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.profac.app.domain.enumeration.UserType;
import com.profac.app.domain.enumeration.AppUserStatus;
import jakarta.validation.constraints.*;
import java.io.Serializable;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

/**
 * A AppUser.
 */
@Table("app_user")
@SuppressWarnings("common-java:DuplicatedBlocks")
public class AppUser extends AbstractAuditingEntity<Long> implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @Column("id")
    private Long id;

    @Column("first_name")
    private String firstName;

    @Column("last_name")
    private String lastName;

    @Column("password")
    private String password;

    @NotNull(message = "must not be null")
    @Column("phone_number")
    private String phoneNumber;

    @Column("address")
    private String address;

    @Column("user_type")
    private UserType userType;

    @Column("status")
    private AppUserStatus status;

    @Transient
    private Image avatar;

    @Transient
    @JsonIgnoreProperties(value = { "appUsers" }, allowSetters = true)
    private Company company;

    @Column("avatar_id")
    private Long avatarId;

    @Column("company_id")
    private Long companyId;

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public AppUser id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getFirstName() {
        return this.firstName;
    }

    public AppUser firstName(String firstName) {
        this.setFirstName(firstName);
        return this;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return this.lastName;
    }

    public AppUser lastName(String lastName) {
        this.setLastName(lastName);
        return this;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getPassword() {
        return this.password;
    }

    public AppUser password(String password) {
        this.setPassword(password);
        return this;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getPhoneNumber() {
        return this.phoneNumber;
    }

    public AppUser phoneNumber(String phoneNumber) {
        this.setPhoneNumber(phoneNumber);
        return this;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getAddress() {
        return this.address;
    }

    public AppUser address(String address) {
        this.setAddress(address);
        return this;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public UserType getUserType() {
        return this.userType;
    }

    public AppUser userType(UserType userType) {
        this.setUserType(userType);
        return this;
    }

    public void setUserType(UserType userType) {
        this.userType = userType;
    }

    public AppUserStatus getStatus() {
        return this.status;
    }

    public AppUser status(AppUserStatus status) {
        this.setStatus(status);
        return this;
    }

    public void setStatus(AppUserStatus status) {
        this.status = status;
    }

    public Image getAvatar() {
        return this.avatar;
    }

    public void setAvatar(Image image) {
        this.avatar = image;
        this.avatarId = image != null ? image.getId() : null;
    }

    public AppUser avatar(Image image) {
        this.setAvatar(image);
        return this;
    }

    public Company getCompany() {
        return this.company;
    }

    public void setCompany(Company company) {
        this.company = company;
        this.companyId = company != null ? company.getId() : null;
    }

    public AppUser company(Company company) {
        this.setCompany(company);
        return this;
    }

    public Long getAvatarId() {
        return this.avatarId;
    }

    public void setAvatarId(Long image) {
        this.avatarId = image;
    }

    public Long getCompanyId() {
        return this.companyId;
    }

    public void setCompanyId(Long company) {
        this.companyId = company;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof AppUser)) {
            return false;
        }
        return getId() != null && getId().equals(((AppUser) o).getId());
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "AppUser{" +
            "id=" + getId() +
            ", firstName='" + getFirstName() + "'" +
            ", lastName='" + getLastName() + "'" +
            ", password='" + getPassword() + "'" +
            ", phoneNumber='" + getPhoneNumber() + "'" +
            ", address='" + getAddress() + "'" +
            ", userType='" + getUserType() + "'" +
            ", status='" + getStatus() + "'" +
            "}";
    }
}
