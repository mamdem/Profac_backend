package com.profac.app.domain;

import static com.profac.app.domain.InvoiceProductTestSamples.*;
import static com.profac.app.domain.InvoiceTestSamples.*;
import static com.profac.app.domain.ProductTestSamples.*;
import static org.assertj.core.api.Assertions.assertThat;

import com.profac.app.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class InvoiceProductTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(InvoiceProduct.class);
        InvoiceProduct invoiceProduct1 = getInvoiceProductSample1();
        InvoiceProduct invoiceProduct2 = new InvoiceProduct();
        assertThat(invoiceProduct1).isNotEqualTo(invoiceProduct2);

        invoiceProduct2.setId(invoiceProduct1.getId());
        assertThat(invoiceProduct1).isEqualTo(invoiceProduct2);

        invoiceProduct2 = getInvoiceProductSample2();
        assertThat(invoiceProduct1).isNotEqualTo(invoiceProduct2);
    }

    @Test
    void invoiceTest() throws Exception {
        InvoiceProduct invoiceProduct = getInvoiceProductRandomSampleGenerator();
        Invoice invoiceBack = getInvoiceRandomSampleGenerator();

        invoiceProduct.setInvoice(invoiceBack);
        assertThat(invoiceProduct.getInvoice()).isEqualTo(invoiceBack);

        invoiceProduct.invoice(null);
        assertThat(invoiceProduct.getInvoice()).isNull();
    }

    @Test
    void productTest() throws Exception {
        InvoiceProduct invoiceProduct = getInvoiceProductRandomSampleGenerator();
        Product productBack = getProductRandomSampleGenerator();

        invoiceProduct.setProduct(productBack);
        assertThat(invoiceProduct.getProduct()).isEqualTo(productBack);

        invoiceProduct.product(null);
        assertThat(invoiceProduct.getProduct()).isNull();
    }
}
