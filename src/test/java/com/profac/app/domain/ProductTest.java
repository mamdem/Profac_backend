package com.profac.app.domain;

import static com.profac.app.domain.CategoryTestSamples.*;
import static com.profac.app.domain.ImageTestSamples.*;
import static com.profac.app.domain.InvoiceProductTestSamples.*;
import static com.profac.app.domain.ProductTestSamples.*;
import static com.profac.app.domain.StockTestSamples.*;
import static org.assertj.core.api.Assertions.assertThat;

import com.profac.app.web.rest.TestUtil;
import java.util.HashSet;
import java.util.Set;
import org.junit.jupiter.api.Test;

class ProductTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(Product.class);
        Product product1 = getProductSample1();
        Product product2 = new Product();
        assertThat(product1).isNotEqualTo(product2);

        product2.setId(product1.getId());
        assertThat(product1).isEqualTo(product2);

        product2 = getProductSample2();
        assertThat(product1).isNotEqualTo(product2);
    }

    @Test
    void stocksTest() throws Exception {
        Product product = getProductRandomSampleGenerator();
        Stock stockBack = getStockRandomSampleGenerator();

        product.addStocks(stockBack);
        assertThat(product.getStocks()).containsOnly(stockBack);
        assertThat(stockBack.getProduct()).isEqualTo(product);

        product.removeStocks(stockBack);
        assertThat(product.getStocks()).doesNotContain(stockBack);
        assertThat(stockBack.getProduct()).isNull();

        product.stocks(new HashSet<>(Set.of(stockBack)));
        assertThat(product.getStocks()).containsOnly(stockBack);
        assertThat(stockBack.getProduct()).isEqualTo(product);

        product.setStocks(new HashSet<>());
        assertThat(product.getStocks()).doesNotContain(stockBack);
        assertThat(stockBack.getProduct()).isNull();
    }

    @Test
    void imagesTest() throws Exception {
        Product product = getProductRandomSampleGenerator();
        Image imageBack = getImageRandomSampleGenerator();

        product.addImages(imageBack);
        assertThat(product.getImages()).containsOnly(imageBack);
        assertThat(imageBack.getProduct()).isEqualTo(product);

        product.removeImages(imageBack);
        assertThat(product.getImages()).doesNotContain(imageBack);
        assertThat(imageBack.getProduct()).isNull();

        product.images(new HashSet<>(Set.of(imageBack)));
        assertThat(product.getImages()).containsOnly(imageBack);
        assertThat(imageBack.getProduct()).isEqualTo(product);

        product.setImages(new HashSet<>());
        assertThat(product.getImages()).doesNotContain(imageBack);
        assertThat(imageBack.getProduct()).isNull();
    }

    @Test
    void categoryTest() throws Exception {
        Product product = getProductRandomSampleGenerator();
        Category categoryBack = getCategoryRandomSampleGenerator();

        product.setCategory(categoryBack);
        assertThat(product.getCategory()).isEqualTo(categoryBack);

        product.category(null);
        assertThat(product.getCategory()).isNull();
    }

    @Test
    void invoiceProductsTest() throws Exception {
        Product product = getProductRandomSampleGenerator();
        InvoiceProduct invoiceProductBack = getInvoiceProductRandomSampleGenerator();

        product.addInvoiceProducts(invoiceProductBack);
        assertThat(product.getInvoiceProducts()).containsOnly(invoiceProductBack);
        assertThat(invoiceProductBack.getProduct()).isEqualTo(product);

        product.removeInvoiceProducts(invoiceProductBack);
        assertThat(product.getInvoiceProducts()).doesNotContain(invoiceProductBack);
        assertThat(invoiceProductBack.getProduct()).isNull();

        product.invoiceProducts(new HashSet<>(Set.of(invoiceProductBack)));
        assertThat(product.getInvoiceProducts()).containsOnly(invoiceProductBack);
        assertThat(invoiceProductBack.getProduct()).isEqualTo(product);

        product.setInvoiceProducts(new HashSet<>());
        assertThat(product.getInvoiceProducts()).doesNotContain(invoiceProductBack);
        assertThat(invoiceProductBack.getProduct()).isNull();
    }
}
