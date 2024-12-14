package com.profac.app.service.dto;

import com.profac.app.domain.AbstractAuditingEntity;
import com.profac.app.domain.enumeration.CompanyStatus;
import jakarta.validation.constraints.*;
import java.io.Serializable;
import java.time.Instant;
import java.util.Objects;

/**
 * A DTO for the {@link com.profac.app.domain.Company} entity.
 */
@SuppressWarnings("common-java:DuplicatedBlocks")
public class CompanyDTO extends AbstractAuditingEntity<Long> implements Serializable {

    private Long id;

    @NotNull(message = "must not be null")
    private String name;

    private Instant validUntil;

    private CompanyStatus status;

    private String password;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Instant getValidUntil() {
        return validUntil;
    }

    public void setValidUntil(Instant validUntil) {
        this.validUntil = validUntil;
    }

    public CompanyStatus getStatus() {
        return status;
    }

    public void setStatus(CompanyStatus status) {
        this.status = status;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof CompanyDTO)) {
            return false;
        }

        CompanyDTO companyDTO = (CompanyDTO) o;
        if (this.id == null) {
            return false;
        }
        return Objects.equals(this.id, companyDTO.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.id);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "CompanyDTO{" +
            "id=" + getId() +
            ", name='" + getName() + "'" +
            ", validUntil='" + getValidUntil() + "'" +
            ", status='" + getStatus() + "'" +
            ", password='" + getPassword() + "'" +
            "}";
    }
}
