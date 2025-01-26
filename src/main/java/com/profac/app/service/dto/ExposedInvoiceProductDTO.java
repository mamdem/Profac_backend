package com.profac.app.service.dto;

public class ExposedInvoiceProductDTO {
    private Integer productNumber;
    private Integer quantity;

    public Integer getProductNumber() {
        return productNumber;
    }

    public void setProductNumber(Integer productNumber) {
        this.productNumber = productNumber;
    }

    public Integer getQuantity() {
        return quantity;
    }

    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }

    @Override
    public String toString() {
        return "ExposedInvoiceProductDTO{" +
            "productNumber=" + productNumber +
            ", quantity=" + quantity +
            '}';
    }
}
