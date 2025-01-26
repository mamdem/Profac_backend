package com.profac.app.web.rest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.is;

import com.profac.app.IntegrationTest;
import com.profac.app.domain.Invoice;
import com.profac.app.domain.enumeration.InvoiceStatus;
import com.profac.app.repository.EntityManager;
import com.profac.app.repository.InvoiceRepository;
import com.profac.app.service.dto.InvoiceDTO;
import com.profac.app.service.mapper.InvoiceMapper;
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
 * Integration tests for the {@link InvoiceResource} REST controller.
 */
@IntegrationTest
@AutoConfigureWebTestClient(timeout = IntegrationTest.DEFAULT_ENTITY_TIMEOUT)
@WithMockUser
class InvoiceResourceIT {

    private static final Long DEFAULT_INVOICE_NUMBER = 1L;
    private static final Long UPDATED_INVOICE_NUMBER = 2L;

    private static final String DEFAULT_CUSTOMER = "AAAAAAAAAA";
    private static final String UPDATED_CUSTOMER = "BBBBBBBBBB";

    private static final String DEFAULT_INVOICE_DATE = "AAAAAAAAAA";
    private static final String UPDATED_INVOICE_DATE = "BBBBBBBBBB";

    private static final InvoiceStatus DEFAULT_STATUS = InvoiceStatus.PENDING;
    private static final InvoiceStatus UPDATED_STATUS = InvoiceStatus.PAID;

    private static final String ENTITY_API_URL = "/api/invoices";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static Random random = new Random();
    private static AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private InvoiceRepository invoiceRepository;

    @Autowired
    private InvoiceMapper invoiceMapper;

    @Autowired
    private EntityManager em;

    @Autowired
    private WebTestClient webTestClient;

    private Invoice invoice;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Invoice createEntity(EntityManager em) {
        Invoice invoice = new Invoice()
            .invoiceNumber(DEFAULT_INVOICE_NUMBER)
            .customer(DEFAULT_CUSTOMER)
            .invoiceDate(DEFAULT_INVOICE_DATE)
            .status(DEFAULT_STATUS);
        return invoice;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Invoice createUpdatedEntity(EntityManager em) {
        Invoice invoice = new Invoice()
            .invoiceNumber(UPDATED_INVOICE_NUMBER)
            .customer(UPDATED_CUSTOMER)
            .invoiceDate(UPDATED_INVOICE_DATE)
            .status(UPDATED_STATUS);
        return invoice;
    }

    public static void deleteEntities(EntityManager em) {
        try {
            em.deleteAll(Invoice.class).block();
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
        invoice = createEntity(em);
    }

    @Test
    void createInvoice() throws Exception {
        int databaseSizeBeforeCreate = invoiceRepository.findAll().collectList().block().size();
        // Create the Invoice
        InvoiceDTO invoiceDTO = invoiceMapper.toDto(invoice);
        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(invoiceDTO))
            .exchange()
            .expectStatus()
            .isCreated();

        // Validate the Invoice in the database
        List<Invoice> invoiceList = invoiceRepository.findAll().collectList().block();
        assertThat(invoiceList).hasSize(databaseSizeBeforeCreate + 1);
        Invoice testInvoice = invoiceList.get(invoiceList.size() - 1);
        assertThat(testInvoice.getInvoiceNumber()).isEqualTo(DEFAULT_INVOICE_NUMBER);
        assertThat(testInvoice.getCustomer()).isEqualTo(DEFAULT_CUSTOMER);
        assertThat(testInvoice.getInvoiceDate()).isEqualTo(DEFAULT_INVOICE_DATE);
        assertThat(testInvoice.getStatus()).isEqualTo(DEFAULT_STATUS);
    }

    @Test
    void createInvoiceWithExistingId() throws Exception {
        // Create the Invoice with an existing ID
        invoice.setId(1L);
        InvoiceDTO invoiceDTO = invoiceMapper.toDto(invoice);

        int databaseSizeBeforeCreate = invoiceRepository.findAll().collectList().block().size();

        // An entity with an existing ID cannot be created, so this API call must fail
        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(invoiceDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Invoice in the database
        List<Invoice> invoiceList = invoiceRepository.findAll().collectList().block();
        assertThat(invoiceList).hasSize(databaseSizeBeforeCreate);
    }

    @Test
    void getAllInvoices() {
        // Initialize the database
        invoiceRepository.save(invoice).block();

        // Get all the invoiceList
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
            .value(hasItem(invoice.getId().intValue()))
            .jsonPath("$.[*].invoiceNumber")
            .value(hasItem(DEFAULT_INVOICE_NUMBER.intValue()))
            .jsonPath("$.[*].customer")
            .value(hasItem(DEFAULT_CUSTOMER))
            .jsonPath("$.[*].invoiceDate")
            .value(hasItem(DEFAULT_INVOICE_DATE))
            .jsonPath("$.[*].status")
            .value(hasItem(DEFAULT_STATUS.toString()));
    }

    @Test
    void getInvoice() {
        // Initialize the database
        invoiceRepository.save(invoice).block();

        // Get the invoice
        webTestClient
            .get()
            .uri(ENTITY_API_URL_ID, invoice.getId())
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isOk()
            .expectHeader()
            .contentType(MediaType.APPLICATION_JSON)
            .expectBody()
            .jsonPath("$.id")
            .value(is(invoice.getId().intValue()))
            .jsonPath("$.invoiceNumber")
            .value(is(DEFAULT_INVOICE_NUMBER.intValue()))
            .jsonPath("$.customer")
            .value(is(DEFAULT_CUSTOMER))
            .jsonPath("$.invoiceDate")
            .value(is(DEFAULT_INVOICE_DATE))
            .jsonPath("$.status")
            .value(is(DEFAULT_STATUS.toString()));
    }

    @Test
    void getNonExistingInvoice() {
        // Get the invoice
        webTestClient
            .get()
            .uri(ENTITY_API_URL_ID, Long.MAX_VALUE)
            .accept(MediaType.APPLICATION_PROBLEM_JSON)
            .exchange()
            .expectStatus()
            .isNotFound();
    }

    @Test
    void putExistingInvoice() throws Exception {
        // Initialize the database
        invoiceRepository.save(invoice).block();

        int databaseSizeBeforeUpdate = invoiceRepository.findAll().collectList().block().size();

        // Update the invoice
        Invoice updatedInvoice = invoiceRepository.findById(invoice.getId()).block();
        updatedInvoice
            .invoiceNumber(UPDATED_INVOICE_NUMBER)
            .customer(UPDATED_CUSTOMER)
            .invoiceDate(UPDATED_INVOICE_DATE)
            .status(UPDATED_STATUS);
        InvoiceDTO invoiceDTO = invoiceMapper.toDto(updatedInvoice);

        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, invoiceDTO.getId())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(invoiceDTO))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the Invoice in the database
        List<Invoice> invoiceList = invoiceRepository.findAll().collectList().block();
        assertThat(invoiceList).hasSize(databaseSizeBeforeUpdate);
        Invoice testInvoice = invoiceList.get(invoiceList.size() - 1);
        assertThat(testInvoice.getInvoiceNumber()).isEqualTo(UPDATED_INVOICE_NUMBER);
        assertThat(testInvoice.getCustomer()).isEqualTo(UPDATED_CUSTOMER);
        assertThat(testInvoice.getInvoiceDate()).isEqualTo(UPDATED_INVOICE_DATE);
        assertThat(testInvoice.getStatus()).isEqualTo(UPDATED_STATUS);
    }

    @Test
    void putNonExistingInvoice() throws Exception {
        int databaseSizeBeforeUpdate = invoiceRepository.findAll().collectList().block().size();
        invoice.setId(longCount.incrementAndGet());

        // Create the Invoice
        InvoiceDTO invoiceDTO = invoiceMapper.toDto(invoice);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, invoiceDTO.getId())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(invoiceDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Invoice in the database
        List<Invoice> invoiceList = invoiceRepository.findAll().collectList().block();
        assertThat(invoiceList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void putWithIdMismatchInvoice() throws Exception {
        int databaseSizeBeforeUpdate = invoiceRepository.findAll().collectList().block().size();
        invoice.setId(longCount.incrementAndGet());

        // Create the Invoice
        InvoiceDTO invoiceDTO = invoiceMapper.toDto(invoice);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, longCount.incrementAndGet())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(invoiceDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Invoice in the database
        List<Invoice> invoiceList = invoiceRepository.findAll().collectList().block();
        assertThat(invoiceList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void putWithMissingIdPathParamInvoice() throws Exception {
        int databaseSizeBeforeUpdate = invoiceRepository.findAll().collectList().block().size();
        invoice.setId(longCount.incrementAndGet());

        // Create the Invoice
        InvoiceDTO invoiceDTO = invoiceMapper.toDto(invoice);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(invoiceDTO))
            .exchange()
            .expectStatus()
            .isEqualTo(405);

        // Validate the Invoice in the database
        List<Invoice> invoiceList = invoiceRepository.findAll().collectList().block();
        assertThat(invoiceList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void partialUpdateInvoiceWithPatch() throws Exception {
        // Initialize the database
        invoiceRepository.save(invoice).block();

        int databaseSizeBeforeUpdate = invoiceRepository.findAll().collectList().block().size();

        // Update the invoice using partial update
        Invoice partialUpdatedInvoice = new Invoice();
        partialUpdatedInvoice.setId(invoice.getId());

        partialUpdatedInvoice.invoiceNumber(UPDATED_INVOICE_NUMBER).customer(UPDATED_CUSTOMER).invoiceDate(UPDATED_INVOICE_DATE);

        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, partialUpdatedInvoice.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(partialUpdatedInvoice))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the Invoice in the database
        List<Invoice> invoiceList = invoiceRepository.findAll().collectList().block();
        assertThat(invoiceList).hasSize(databaseSizeBeforeUpdate);
        Invoice testInvoice = invoiceList.get(invoiceList.size() - 1);
        assertThat(testInvoice.getInvoiceNumber()).isEqualTo(UPDATED_INVOICE_NUMBER);
        assertThat(testInvoice.getCustomer()).isEqualTo(UPDATED_CUSTOMER);
        assertThat(testInvoice.getInvoiceDate()).isEqualTo(UPDATED_INVOICE_DATE);
        assertThat(testInvoice.getStatus()).isEqualTo(DEFAULT_STATUS);
    }

    @Test
    void fullUpdateInvoiceWithPatch() throws Exception {
        // Initialize the database
        invoiceRepository.save(invoice).block();

        int databaseSizeBeforeUpdate = invoiceRepository.findAll().collectList().block().size();

        // Update the invoice using partial update
        Invoice partialUpdatedInvoice = new Invoice();
        partialUpdatedInvoice.setId(invoice.getId());

        partialUpdatedInvoice
            .invoiceNumber(UPDATED_INVOICE_NUMBER)
            .customer(UPDATED_CUSTOMER)
            .invoiceDate(UPDATED_INVOICE_DATE)
            .status(UPDATED_STATUS);

        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, partialUpdatedInvoice.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(partialUpdatedInvoice))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the Invoice in the database
        List<Invoice> invoiceList = invoiceRepository.findAll().collectList().block();
        assertThat(invoiceList).hasSize(databaseSizeBeforeUpdate);
        Invoice testInvoice = invoiceList.get(invoiceList.size() - 1);
        assertThat(testInvoice.getInvoiceNumber()).isEqualTo(UPDATED_INVOICE_NUMBER);
        assertThat(testInvoice.getCustomer()).isEqualTo(UPDATED_CUSTOMER);
        assertThat(testInvoice.getInvoiceDate()).isEqualTo(UPDATED_INVOICE_DATE);
        assertThat(testInvoice.getStatus()).isEqualTo(UPDATED_STATUS);
    }

    @Test
    void patchNonExistingInvoice() throws Exception {
        int databaseSizeBeforeUpdate = invoiceRepository.findAll().collectList().block().size();
        invoice.setId(longCount.incrementAndGet());

        // Create the Invoice
        InvoiceDTO invoiceDTO = invoiceMapper.toDto(invoice);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, invoiceDTO.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(invoiceDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Invoice in the database
        List<Invoice> invoiceList = invoiceRepository.findAll().collectList().block();
        assertThat(invoiceList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void patchWithIdMismatchInvoice() throws Exception {
        int databaseSizeBeforeUpdate = invoiceRepository.findAll().collectList().block().size();
        invoice.setId(longCount.incrementAndGet());

        // Create the Invoice
        InvoiceDTO invoiceDTO = invoiceMapper.toDto(invoice);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, longCount.incrementAndGet())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(invoiceDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Invoice in the database
        List<Invoice> invoiceList = invoiceRepository.findAll().collectList().block();
        assertThat(invoiceList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void patchWithMissingIdPathParamInvoice() throws Exception {
        int databaseSizeBeforeUpdate = invoiceRepository.findAll().collectList().block().size();
        invoice.setId(longCount.incrementAndGet());

        // Create the Invoice
        InvoiceDTO invoiceDTO = invoiceMapper.toDto(invoice);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(invoiceDTO))
            .exchange()
            .expectStatus()
            .isEqualTo(405);

        // Validate the Invoice in the database
        List<Invoice> invoiceList = invoiceRepository.findAll().collectList().block();
        assertThat(invoiceList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void deleteInvoice() {
        // Initialize the database
        invoiceRepository.save(invoice).block();

        int databaseSizeBeforeDelete = invoiceRepository.findAll().collectList().block().size();

        // Delete the invoice
        webTestClient
            .delete()
            .uri(ENTITY_API_URL_ID, invoice.getId())
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isNoContent();

        // Validate the database contains one less item
        List<Invoice> invoiceList = invoiceRepository.findAll().collectList().block();
        assertThat(invoiceList).hasSize(databaseSizeBeforeDelete - 1);
    }
}
