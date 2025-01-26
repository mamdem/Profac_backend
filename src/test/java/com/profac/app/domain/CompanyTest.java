package com.profac.app.domain;

import static com.profac.app.domain.AppUserTestSamples.*;
import static com.profac.app.domain.CompanyTestSamples.*;
import static com.profac.app.domain.InvoiceTestSamples.*;
import static com.profac.app.domain.StockTestSamples.*;
import static org.assertj.core.api.Assertions.assertThat;

import com.profac.app.web.rest.TestUtil;
import java.util.HashSet;
import java.util.Set;
import org.junit.jupiter.api.Test;

class CompanyTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(Company.class);
        Company company1 = getCompanySample1();
        Company company2 = new Company();
        assertThat(company1).isNotEqualTo(company2);

        company2.setId(company1.getId());
        assertThat(company1).isEqualTo(company2);

        company2 = getCompanySample2();
        assertThat(company1).isNotEqualTo(company2);
    }

    @Test
    void appUsersTest() throws Exception {
        Company company = getCompanyRandomSampleGenerator();
        AppUser appUserBack = getAppUserRandomSampleGenerator();

        company.addAppUsers(appUserBack);
        assertThat(company.getAppUsers()).containsOnly(appUserBack);
        assertThat(appUserBack.getCompany()).isEqualTo(company);

        company.removeAppUsers(appUserBack);
        assertThat(company.getAppUsers()).doesNotContain(appUserBack);
        assertThat(appUserBack.getCompany()).isNull();

        company.appUsers(new HashSet<>(Set.of(appUserBack)));
        assertThat(company.getAppUsers()).containsOnly(appUserBack);
        assertThat(appUserBack.getCompany()).isEqualTo(company);

        company.setAppUsers(new HashSet<>());
        assertThat(company.getAppUsers()).doesNotContain(appUserBack);
        assertThat(appUserBack.getCompany()).isNull();
    }

    @Test
    void invoicesTest() throws Exception {
        Company company = getCompanyRandomSampleGenerator();
        Invoice invoiceBack = getInvoiceRandomSampleGenerator();

        company.addInvoices(invoiceBack);
        assertThat(company.getInvoices()).containsOnly(invoiceBack);
        assertThat(invoiceBack.getCompany()).isEqualTo(company);

        company.removeInvoices(invoiceBack);
        assertThat(company.getInvoices()).doesNotContain(invoiceBack);
        assertThat(invoiceBack.getCompany()).isNull();

        company.invoices(new HashSet<>(Set.of(invoiceBack)));
        assertThat(company.getInvoices()).containsOnly(invoiceBack);
        assertThat(invoiceBack.getCompany()).isEqualTo(company);

        company.setInvoices(new HashSet<>());
        assertThat(company.getInvoices()).doesNotContain(invoiceBack);
        assertThat(invoiceBack.getCompany()).isNull();
    }

    @Test
    void stocksTest() throws Exception {
        Company company = getCompanyRandomSampleGenerator();
        Stock stockBack = getStockRandomSampleGenerator();

        company.addStocks(stockBack);
        assertThat(company.getStocks()).containsOnly(stockBack);
        assertThat(stockBack.getCompany()).isEqualTo(company);

        company.removeStocks(stockBack);
        assertThat(company.getStocks()).doesNotContain(stockBack);
        assertThat(stockBack.getCompany()).isNull();

        company.stocks(new HashSet<>(Set.of(stockBack)));
        assertThat(company.getStocks()).containsOnly(stockBack);
        assertThat(stockBack.getCompany()).isEqualTo(company);

        company.setStocks(new HashSet<>());
        assertThat(company.getStocks()).doesNotContain(stockBack);
        assertThat(stockBack.getCompany()).isNull();
    }
}
