package com.profac.app.domain;

import static com.profac.app.domain.CompanyTestSamples.*;
import static com.profac.app.domain.ProductTestSamples.*;
import static com.profac.app.domain.StockTestSamples.*;
import static org.assertj.core.api.Assertions.assertThat;

import com.profac.app.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class StockTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(Stock.class);
        Stock stock1 = getStockSample1();
        Stock stock2 = new Stock();
        assertThat(stock1).isNotEqualTo(stock2);

        stock2.setId(stock1.getId());
        assertThat(stock1).isEqualTo(stock2);

        stock2 = getStockSample2();
        assertThat(stock1).isNotEqualTo(stock2);
    }

    @Test
    void companyTest() throws Exception {
        Stock stock = getStockRandomSampleGenerator();
        Company companyBack = getCompanyRandomSampleGenerator();

        stock.setCompany(companyBack);
        assertThat(stock.getCompany()).isEqualTo(companyBack);

        stock.company(null);
        assertThat(stock.getCompany()).isNull();
    }

    @Test
    void productTest() throws Exception {
        Stock stock = getStockRandomSampleGenerator();
        Product productBack = getProductRandomSampleGenerator();

        stock.setProduct(productBack);
        assertThat(stock.getProduct()).isEqualTo(productBack);

        stock.product(null);
        assertThat(stock.getProduct()).isNull();
    }
}
