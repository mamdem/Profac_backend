package com.profac.app.domain;

import static com.profac.app.domain.CompanyTestSamples.*;
import static com.profac.app.domain.InvoiceProductTestSamples.*;
import static com.profac.app.domain.InvoiceTestSamples.*;
import static org.assertj.core.api.Assertions.assertThat;

import com.profac.app.web.rest.TestUtil;
import java.util.HashSet;
import java.util.Set;
import org.junit.jupiter.api.Test;

class InvoiceTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(Invoice.class);
        Invoice invoice1 = getInvoiceSample1();
        Invoice invoice2 = new Invoice();
        assertThat(invoice1).isNotEqualTo(invoice2);

        invoice2.setId(invoice1.getId());
        assertThat(invoice1).isEqualTo(invoice2);

        invoice2 = getInvoiceSample2();
        assertThat(invoice1).isNotEqualTo(invoice2);
    }

    @Test
    void companyTest() throws Exception {
        Invoice invoice = getInvoiceRandomSampleGenerator();
        Company companyBack = getCompanyRandomSampleGenerator();

        invoice.setCompany(companyBack);
        assertThat(invoice.getCompany()).isEqualTo(companyBack);

        invoice.company(null);
        assertThat(invoice.getCompany()).isNull();
    }

    @Test
    void invoiceProductsTest() throws Exception {
        Invoice invoice = getInvoiceRandomSampleGenerator();
        InvoiceProduct invoiceProductBack = getInvoiceProductRandomSampleGenerator();

        invoice.addInvoiceProducts(invoiceProductBack);
        assertThat(invoice.getInvoiceProducts()).containsOnly(invoiceProductBack);
        assertThat(invoiceProductBack.getInvoice()).isEqualTo(invoice);

        invoice.removeInvoiceProducts(invoiceProductBack);
        assertThat(invoice.getInvoiceProducts()).doesNotContain(invoiceProductBack);
        assertThat(invoiceProductBack.getInvoice()).isNull();

        invoice.invoiceProducts(new HashSet<>(Set.of(invoiceProductBack)));
        assertThat(invoice.getInvoiceProducts()).containsOnly(invoiceProductBack);
        assertThat(invoiceProductBack.getInvoice()).isEqualTo(invoice);

        invoice.setInvoiceProducts(new HashSet<>());
        assertThat(invoice.getInvoiceProducts()).doesNotContain(invoiceProductBack);
        assertThat(invoiceProductBack.getInvoice()).isNull();
    }
}
