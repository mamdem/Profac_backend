package com.profac.app.web.rest;

import static com.profac.app.web.rest.TestUtil.sameNumber;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.is;

import com.profac.app.IntegrationTest;
import com.profac.app.domain.InvoiceProduct;
import com.profac.app.repository.EntityManager;
import com.profac.app.repository.InvoiceProductRepository;
import com.profac.app.service.dto.InvoiceProductDTO;
import com.profac.app.service.mapper.InvoiceProductMapper;
import java.math.BigDecimal;
import java.util.List;
import java.util.Random;
import java.util.concurrent.atomic.AtomicLong;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.reactive.server.WebTestClient;

/**
 * Integration tests for the {@link InvoiceProductResource} REST controller.
 */
@IntegrationTest
@AutoConfigureWebTestClient(timeout = IntegrationTest.DEFAULT_ENTITY_TIMEOUT)
@WithMockUser
class InvoiceProductResourceIT {

    private static final Integer DEFAULT_QUANTITY = 1;
    private static final Integer UPDATED_QUANTITY = 2;

    private static final BigDecimal DEFAULT_TOTAL_AMOUNT = new BigDecimal(1);
    private static final BigDecimal UPDATED_TOTAL_AMOUNT = new BigDecimal(2);

    private static final String ENTITY_API_URL = "/api/invoice-products";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static Random random = new Random();
    private static AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private InvoiceProductRepository invoiceProductRepository;

    @Autowired
    private InvoiceProductMapper invoiceProductMapper;

    @Autowired
    private EntityManager em;

    @Autowired
    private WebTestClient webTestClient;

    private InvoiceProduct invoiceProduct;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static InvoiceProduct createEntity(EntityManager em) {
        InvoiceProduct invoiceProduct = new InvoiceProduct().quantity(DEFAULT_QUANTITY).totalAmount(DEFAULT_TOTAL_AMOUNT);
        return invoiceProduct;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static InvoiceProduct createUpdatedEntity(EntityManager em) {
        InvoiceProduct invoiceProduct = new InvoiceProduct().quantity(UPDATED_QUANTITY).totalAmount(UPDATED_TOTAL_AMOUNT);
        return invoiceProduct;
    }

    public static void deleteEntities(EntityManager em) {
        try {
            em.deleteAll(InvoiceProduct.class).block();
        } catch (Exception e) {
            // It can fail, if other entities are still referring this - it will be removed later.
        }
    }

    @AfterEach
    public void cleanup() {
        deleteEntities(em);
    }

    @BeforeEach
    public void initTest() {
        deleteEntities(em);
        invoiceProduct = createEntity(em);
    }

    @Test
    void createInvoiceProduct() throws Exception {
        int databaseSizeBeforeCreate = invoiceProductRepository.findAll().collectList().block().size();
        // Create the InvoiceProduct
        InvoiceProductDTO invoiceProductDTO = invoiceProductMapper.toDto(invoiceProduct);
        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(invoiceProductDTO))
            .exchange()
            .expectStatus()
            .isCreated();

        // Validate the InvoiceProduct in the database
        List<InvoiceProduct> invoiceProductList = invoiceProductRepository.findAll().collectList().block();
        assertThat(invoiceProductList).hasSize(databaseSizeBeforeCreate + 1);
        InvoiceProduct testInvoiceProduct = invoiceProductList.get(invoiceProductList.size() - 1);
        assertThat(testInvoiceProduct.getQuantity()).isEqualTo(DEFAULT_QUANTITY);
        assertThat(testInvoiceProduct.getTotalAmount()).isEqualByComparingTo(DEFAULT_TOTAL_AMOUNT);
    }

    @Test
    void createInvoiceProductWithExistingId() throws Exception {
        // Create the InvoiceProduct with an existing ID
        invoiceProduct.setId(1L);
        InvoiceProductDTO invoiceProductDTO = invoiceProductMapper.toDto(invoiceProduct);

        int databaseSizeBeforeCreate = invoiceProductRepository.findAll().collectList().block().size();

        // An entity with an existing ID cannot be created, so this API call must fail
        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(invoiceProductDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the InvoiceProduct in the database
        List<InvoiceProduct> invoiceProductList = invoiceProductRepository.findAll().collectList().block();
        assertThat(invoiceProductList).hasSize(databaseSizeBeforeCreate);
    }

    @Test
    void checkQuantityIsRequired() throws Exception {
        int databaseSizeBeforeTest = invoiceProductRepository.findAll().collectList().block().size();
        // set the field null
        invoiceProduct.setQuantity(null);

        // Create the InvoiceProduct, which fails.
        InvoiceProductDTO invoiceProductDTO = invoiceProductMapper.toDto(invoiceProduct);

        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(invoiceProductDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        List<InvoiceProduct> invoiceProductList = invoiceProductRepository.findAll().collectList().block();
        assertThat(invoiceProductList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    void getAllInvoiceProducts() {
        // Initialize the database
        invoiceProductRepository.save(invoiceProduct).block();

        // Get all the invoiceProductList
        webTestClient
            .get()
            .uri(ENTITY_API_URL + "?sort=id,desc")
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isOk()
            .expectHeader()
            .contentType(MediaType.APPLICATION_JSON)
            .expectBody()
            .jsonPath("$.[*].id")
            .value(hasItem(invoiceProduct.getId().intValue()))
            .jsonPath("$.[*].quantity")
            .value(hasItem(DEFAULT_QUANTITY))
            .jsonPath("$.[*].totalAmount")
            .value(hasItem(sameNumber(DEFAULT_TOTAL_AMOUNT)));
    }

    @Test
    void getInvoiceProduct() {
        // Initialize the database
        invoiceProductRepository.save(invoiceProduct).block();

        // Get the invoiceProduct
        webTestClient
            .get()
            .uri(ENTITY_API_URL_ID, invoiceProduct.getId())
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isOk()
            .expectHeader()
            .contentType(MediaType.APPLICATION_JSON)
            .expectBody()
            .jsonPath("$.id")
            .value(is(invoiceProduct.getId().intValue()))
            .jsonPath("$.quantity")
            .value(is(DEFAULT_QUANTITY))
            .jsonPath("$.totalAmount")
            .value(is(sameNumber(DEFAULT_TOTAL_AMOUNT)));
    }

    @Test
    void getNonExistingInvoiceProduct() {
        // Get the invoiceProduct
        webTestClient
            .get()
            .uri(ENTITY_API_URL_ID, Long.MAX_VALUE)
            .accept(MediaType.APPLICATION_PROBLEM_JSON)
            .exchange()
            .expectStatus()
            .isNotFound();
    }

    @Test
    void putExistingInvoiceProduct() throws Exception {
        // Initialize the database
        invoiceProductRepository.save(invoiceProduct).block();

        int databaseSizeBeforeUpdate = invoiceProductRepository.findAll().collectList().block().size();

        // Update the invoiceProduct
        InvoiceProduct updatedInvoiceProduct = invoiceProductRepository.findById(invoiceProduct.getId()).block();
        updatedInvoiceProduct.quantity(UPDATED_QUANTITY).totalAmount(UPDATED_TOTAL_AMOUNT);
        InvoiceProductDTO invoiceProductDTO = invoiceProductMapper.toDto(updatedInvoiceProduct);

        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, invoiceProductDTO.getId())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(invoiceProductDTO))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the InvoiceProduct in the database
        List<InvoiceProduct> invoiceProductList = invoiceProductRepository.findAll().collectList().block();
        assertThat(invoiceProductList).hasSize(databaseSizeBeforeUpdate);
        InvoiceProduct testInvoiceProduct = invoiceProductList.get(invoiceProductList.size() - 1);
        assertThat(testInvoiceProduct.getQuantity()).isEqualTo(UPDATED_QUANTITY);
        assertThat(testInvoiceProduct.getTotalAmount()).isEqualByComparingTo(UPDATED_TOTAL_AMOUNT);
    }

    @Test
    void putNonExistingInvoiceProduct() throws Exception {
        int databaseSizeBeforeUpdate = invoiceProductRepository.findAll().collectList().block().size();
        invoiceProduct.setId(longCount.incrementAndGet());

        // Create the InvoiceProduct
        InvoiceProductDTO invoiceProductDTO = invoiceProductMapper.toDto(invoiceProduct);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, invoiceProductDTO.getId())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(invoiceProductDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the InvoiceProduct in the database
        List<InvoiceProduct> invoiceProductList = invoiceProductRepository.findAll().collectList().block();
        assertThat(invoiceProductList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void putWithIdMismatchInvoiceProduct() throws Exception {
        int databaseSizeBeforeUpdate = invoiceProductRepository.findAll().collectList().block().size();
        invoiceProduct.setId(longCount.incrementAndGet());

        // Create the InvoiceProduct
        InvoiceProductDTO invoiceProductDTO = invoiceProductMapper.toDto(invoiceProduct);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, longCount.incrementAndGet())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(invoiceProductDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the InvoiceProduct in the database
        List<InvoiceProduct> invoiceProductList = invoiceProductRepository.findAll().collectList().block();
        assertThat(invoiceProductList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void putWithMissingIdPathParamInvoiceProduct() throws Exception {
        int databaseSizeBeforeUpdate = invoiceProductRepository.findAll().collectList().block().size();
        invoiceProduct.setId(longCount.incrementAndGet());

        // Create the InvoiceProduct
        InvoiceProductDTO invoiceProductDTO = invoiceProductMapper.toDto(invoiceProduct);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(invoiceProductDTO))
            .exchange()
            .expectStatus()
            .isEqualTo(405);

        // Validate the InvoiceProduct in the database
        List<InvoiceProduct> invoiceProductList = invoiceProductRepository.findAll().collectList().block();
        assertThat(invoiceProductList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void partialUpdateInvoiceProductWithPatch() throws Exception {
        // Initialize the database
        invoiceProductRepository.save(invoiceProduct).block();

        int databaseSizeBeforeUpdate = invoiceProductRepository.findAll().collectList().block().size();

        // Update the invoiceProduct using partial update
        InvoiceProduct partialUpdatedInvoiceProduct = new InvoiceProduct();
        partialUpdatedInvoiceProduct.setId(invoiceProduct.getId());

        partialUpdatedInvoiceProduct.totalAmount(UPDATED_TOTAL_AMOUNT);

        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, partialUpdatedInvoiceProduct.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(partialUpdatedInvoiceProduct))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the InvoiceProduct in the database
        List<InvoiceProduct> invoiceProductList = invoiceProductRepository.findAll().collectList().block();
        assertThat(invoiceProductList).hasSize(databaseSizeBeforeUpdate);
        InvoiceProduct testInvoiceProduct = invoiceProductList.get(invoiceProductList.size() - 1);
        assertThat(testInvoiceProduct.getQuantity()).isEqualTo(DEFAULT_QUANTITY);
        assertThat(testInvoiceProduct.getTotalAmount()).isEqualByComparingTo(UPDATED_TOTAL_AMOUNT);
    }

    @Test
    void fullUpdateInvoiceProductWithPatch() throws Exception {
        // Initialize the database
        invoiceProductRepository.save(invoiceProduct).block();

        int databaseSizeBeforeUpdate = invoiceProductRepository.findAll().collectList().block().size();

        // Update the invoiceProduct using partial update
        InvoiceProduct partialUpdatedInvoiceProduct = new InvoiceProduct();
        partialUpdatedInvoiceProduct.setId(invoiceProduct.getId());

        partialUpdatedInvoiceProduct.quantity(UPDATED_QUANTITY).totalAmount(UPDATED_TOTAL_AMOUNT);

        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, partialUpdatedInvoiceProduct.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(partialUpdatedInvoiceProduct))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the InvoiceProduct in the database
        List<InvoiceProduct> invoiceProductList = invoiceProductRepository.findAll().collectList().block();
        assertThat(invoiceProductList).hasSize(databaseSizeBeforeUpdate);
        InvoiceProduct testInvoiceProduct = invoiceProductList.get(invoiceProductList.size() - 1);
        assertThat(testInvoiceProduct.getQuantity()).isEqualTo(UPDATED_QUANTITY);
        assertThat(testInvoiceProduct.getTotalAmount()).isEqualByComparingTo(UPDATED_TOTAL_AMOUNT);
    }

    @Test
    void patchNonExistingInvoiceProduct() throws Exception {
        int databaseSizeBeforeUpdate = invoiceProductRepository.findAll().collectList().block().size();
        invoiceProduct.setId(longCount.incrementAndGet());

        // Create the InvoiceProduct
        InvoiceProductDTO invoiceProductDTO = invoiceProductMapper.toDto(invoiceProduct);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, invoiceProductDTO.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(invoiceProductDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the InvoiceProduct in the database
        List<InvoiceProduct> invoiceProductList = invoiceProductRepository.findAll().collectList().block();
        assertThat(invoiceProductList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void patchWithIdMismatchInvoiceProduct() throws Exception {
        int databaseSizeBeforeUpdate = invoiceProductRepository.findAll().collectList().block().size();
        invoiceProduct.setId(longCount.incrementAndGet());

        // Create the InvoiceProduct
        InvoiceProductDTO invoiceProductDTO = invoiceProductMapper.toDto(invoiceProduct);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, longCount.incrementAndGet())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(invoiceProductDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the InvoiceProduct in the database
        List<InvoiceProduct> invoiceProductList = invoiceProductRepository.findAll().collectList().block();
        assertThat(invoiceProductList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void patchWithMissingIdPathParamInvoiceProduct() throws Exception {
        int databaseSizeBeforeUpdate = invoiceProductRepository.findAll().collectList().block().size();
        invoiceProduct.setId(longCount.incrementAndGet());

        // Create the InvoiceProduct
        InvoiceProductDTO invoiceProductDTO = invoiceProductMapper.toDto(invoiceProduct);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(invoiceProductDTO))
            .exchange()
            .expectStatus()
            .isEqualTo(405);

        // Validate the InvoiceProduct in the database
        List<InvoiceProduct> invoiceProductList = invoiceProductRepository.findAll().collectList().block();
        assertThat(invoiceProductList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void deleteInvoiceProduct() {
        // Initialize the database
        invoiceProductRepository.save(invoiceProduct).block();

        int databaseSizeBeforeDelete = invoiceProductRepository.findAll().collectList().block().size();

        // Delete the invoiceProduct
        webTestClient
            .delete()
            .uri(ENTITY_API_URL_ID, invoiceProduct.getId())
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isNoContent();

        // Validate the database contains one less item
        List<InvoiceProduct> invoiceProductList = invoiceProductRepository.findAll().collectList().block();
        assertThat(invoiceProductList).hasSize(databaseSizeBeforeDelete - 1);
    }
}
