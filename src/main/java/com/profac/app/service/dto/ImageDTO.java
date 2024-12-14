package com.profac.app.service.dto;

import com.profac.app.domain.AbstractAuditingEntity;
import com.profac.app.domain.enumeration.ImageStatus;
import jakarta.persistence.Lob;
import java.io.Serializable;
import java.util.Objects;

/**
 * A DTO for the {@link com.profac.app.domain.Image} entity.
 */
@SuppressWarnings("common-java:DuplicatedBlocks")
public class ImageDTO extends AbstractAuditingEntity<Long> implements Serializable {

    private Long id;

    @Lob
    private String url;

    private ImageStatus status;

    private ProductDTO product;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public ImageStatus getStatus() {
        return status;
    }

    public void setStatus(ImageStatus status) {
        this.status = status;
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
        if (!(o instanceof ImageDTO)) {
            return false;
        }

        ImageDTO imageDTO = (ImageDTO) o;
        if (this.id == null) {
            return false;
        }
        return Objects.equals(this.id, imageDTO.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.id);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "ImageDTO{" +
            "id=" + getId() +
            ", url='" + getUrl() + "'" +
            ", status='" + getStatus() + "'" +
            ", product=" + getProduct() +
            "}";
    }
}
