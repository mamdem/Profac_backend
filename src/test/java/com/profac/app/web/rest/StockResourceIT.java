package com.profac.app.web.rest;

import static com.profac.app.web.rest.TestUtil.sameNumber;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.is;

import com.profac.app.IntegrationTest;
import com.profac.app.domain.Stock;
import com.profac.app.domain.enumeration.StockStatus;
import com.profac.app.repository.EntityManager;
import com.profac.app.repository.StockRepository;
import com.profac.app.service.dto.StockDTO;
import com.profac.app.service.mapper.StockMapper;
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
 * Integration tests for the {@link StockResource} REST controller.
 */
@IntegrationTest
@AutoConfigureWebTestClient(timeout = IntegrationTest.DEFAULT_ENTITY_TIMEOUT)
@WithMockUser
class StockResourceIT {

    private static final BigDecimal DEFAULT_TOTAL_AMOUNT = new BigDecimal(1);
    private static final BigDecimal UPDATED_TOTAL_AMOUNT = new BigDecimal(2);

    private static final BigDecimal DEFAULT_TOTAL_AMOUNT_SOLD = new BigDecimal(1);
    private static final BigDecimal UPDATED_TOTAL_AMOUNT_SOLD = new BigDecimal(2);

    private static final Integer DEFAULT_INITIAL_QUANTITY = 1;
    private static final Integer UPDATED_INITIAL_QUANTITY = 2;

    private static final Integer DEFAULT_REMAINING_QUANTITY = 1;
    private static final Integer UPDATED_REMAINING_QUANTITY = 2;

    private static final StockStatus DEFAULT_STATUS = StockStatus.ACTIVE;
    private static final StockStatus UPDATED_STATUS = StockStatus.INACTIVE;

    private static final String ENTITY_API_URL = "/api/stocks";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static Random random = new Random();
    private static AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private StockRepository stockRepository;

    @Autowired
    private StockMapper stockMapper;

    @Autowired
    private EntityManager em;

    @Autowired
    private WebTestClient webTestClient;

    private Stock stock;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Stock createEntity(EntityManager em) {
        Stock stock = new Stock()
            .totalAmount(DEFAULT_TOTAL_AMOUNT)
            .totalAmountSold(DEFAULT_TOTAL_AMOUNT_SOLD)
            .initialQuantity(DEFAULT_INITIAL_QUANTITY)
            .remainingQuantity(DEFAULT_REMAINING_QUANTITY)
            .status(DEFAULT_STATUS);
        return stock;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Stock createUpdatedEntity(EntityManager em) {
        Stock stock = new Stock()
            .totalAmount(UPDATED_TOTAL_AMOUNT)
            .totalAmountSold(UPDATED_TOTAL_AMOUNT_SOLD)
            .initialQuantity(UPDATED_INITIAL_QUANTITY)
            .remainingQuantity(UPDATED_REMAINING_QUANTITY)
            .status(UPDATED_STATUS);
        return stock;
    }

    public static void deleteEntities(EntityManager em) {
        try {
            em.deleteAll(Stock.class).block();
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
        stock = createEntity(em);
    }

    @Test
    void createStock() throws Exception {
        int databaseSizeBeforeCreate = stockRepository.findAll().collectList().block().size();
        // Create the Stock
        StockDTO stockDTO = stockMapper.toDto(stock);
        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(stockDTO))
            .exchange()
            .expectStatus()
            .isCreated();

        // Validate the Stock in the database
        List<Stock> stockList = stockRepository.findAll().collectList().block();
        assertThat(stockList).hasSize(databaseSizeBeforeCreate + 1);
        Stock testStock = stockList.get(stockList.size() - 1);
        assertThat(testStock.getTotalAmount()).isEqualByComparingTo(DEFAULT_TOTAL_AMOUNT);
        assertThat(testStock.getTotalAmountSold()).isEqualByComparingTo(DEFAULT_TOTAL_AMOUNT_SOLD);
        assertThat(testStock.getInitialQuantity()).isEqualTo(DEFAULT_INITIAL_QUANTITY);
        assertThat(testStock.getRemainingQuantity()).isEqualTo(DEFAULT_REMAINING_QUANTITY);
        assertThat(testStock.getStatus()).isEqualTo(DEFAULT_STATUS);
    }

    @Test
    void createStockWithExistingId() throws Exception {
        // Create the Stock with an existing ID
        stock.setId(1L);
        StockDTO stockDTO = stockMapper.toDto(stock);

        int databaseSizeBeforeCreate = stockRepository.findAll().collectList().block().size();

        // An entity with an existing ID cannot be created, so this API call must fail
        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(stockDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Stock in the database
        List<Stock> stockList = stockRepository.findAll().collectList().block();
        assertThat(stockList).hasSize(databaseSizeBeforeCreate);
    }

    @Test
    void checkTotalAmountIsRequired() throws Exception {
        int databaseSizeBeforeTest = stockRepository.findAll().collectList().block().size();
        // set the field null
        stock.setTotalAmount(null);

        // Create the Stock, which fails.
        StockDTO stockDTO = stockMapper.toDto(stock);

        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(stockDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        List<Stock> stockList = stockRepository.findAll().collectList().block();
        assertThat(stockList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    void checkTotalAmountSoldIsRequired() throws Exception {
        int databaseSizeBeforeTest = stockRepository.findAll().collectList().block().size();
        // set the field null
        stock.setTotalAmountSold(null);

        // Create the Stock, which fails.
        StockDTO stockDTO = stockMapper.toDto(stock);

        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(stockDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        List<Stock> stockList = stockRepository.findAll().collectList().block();
        assertThat(stockList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    void checkInitialQuantityIsRequired() throws Exception {
        int databaseSizeBeforeTest = stockRepository.findAll().collectList().block().size();
        // set the field null
        stock.setInitialQuantity(null);

        // Create the Stock, which fails.
        StockDTO stockDTO = stockMapper.toDto(stock);

        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(stockDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        List<Stock> stockList = stockRepository.findAll().collectList().block();
        assertThat(stockList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    void checkRemainingQuantityIsRequired() throws Exception {
        int databaseSizeBeforeTest = stockRepository.findAll().collectList().block().size();
        // set the field null
        stock.setRemainingQuantity(null);

        // Create the Stock, which fails.
        StockDTO stockDTO = stockMapper.toDto(stock);

        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(stockDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        List<Stock> stockList = stockRepository.findAll().collectList().block();
        assertThat(stockList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    void getAllStocks() {
        // Initialize the database
        stockRepository.save(stock).block();

        // Get all the stockList
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
            .value(hasItem(stock.getId().intValue()))
            .jsonPath("$.[*].totalAmount")
            .value(hasItem(sameNumber(DEFAULT_TOTAL_AMOUNT)))
            .jsonPath("$.[*].totalAmountSold")
            .value(hasItem(sameNumber(DEFAULT_TOTAL_AMOUNT_SOLD)))
            .jsonPath("$.[*].initialQuantity")
            .value(hasItem(DEFAULT_INITIAL_QUANTITY))
            .jsonPath("$.[*].remainingQuantity")
            .value(hasItem(DEFAULT_REMAINING_QUANTITY))
            .jsonPath("$.[*].status")
            .value(hasItem(DEFAULT_STATUS.toString()));
    }

    @Test
    void getStock() {
        // Initialize the database
        stockRepository.save(stock).block();

        // Get the stock
        webTestClient
            .get()
            .uri(ENTITY_API_URL_ID, stock.getId())
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isOk()
            .expectHeader()
            .contentType(MediaType.APPLICATION_JSON)
            .expectBody()
            .jsonPath("$.id")
            .value(is(stock.getId().intValue()))
            .jsonPath("$.totalAmount")
            .value(is(sameNumber(DEFAULT_TOTAL_AMOUNT)))
            .jsonPath("$.totalAmountSold")
            .value(is(sameNumber(DEFAULT_TOTAL_AMOUNT_SOLD)))
            .jsonPath("$.initialQuantity")
            .value(is(DEFAULT_INITIAL_QUANTITY))
            .jsonPath("$.remainingQuantity")
            .value(is(DEFAULT_REMAINING_QUANTITY))
            .jsonPath("$.status")
            .value(is(DEFAULT_STATUS.toString()));
    }

    @Test
    void getNonExistingStock() {
        // Get the stock
        webTestClient
            .get()
            .uri(ENTITY_API_URL_ID, Long.MAX_VALUE)
            .accept(MediaType.APPLICATION_PROBLEM_JSON)
            .exchange()
            .expectStatus()
            .isNotFound();
    }

    @Test
    void putExistingStock() throws Exception {
        // Initialize the database
        stockRepository.save(stock).block();

        int databaseSizeBeforeUpdate = stockRepository.findAll().collectList().block().size();

        // Update the stock
        Stock updatedStock = stockRepository.findById(stock.getId()).block();
        updatedStock
            .totalAmount(UPDATED_TOTAL_AMOUNT)
            .totalAmountSold(UPDATED_TOTAL_AMOUNT_SOLD)
            .initialQuantity(UPDATED_INITIAL_QUANTITY)
            .remainingQuantity(UPDATED_REMAINING_QUANTITY)
            .status(UPDATED_STATUS);
        StockDTO stockDTO = stockMapper.toDto(updatedStock);

        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, stockDTO.getId())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(stockDTO))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the Stock in the database
        List<Stock> stockList = stockRepository.findAll().collectList().block();
        assertThat(stockList).hasSize(databaseSizeBeforeUpdate);
        Stock testStock = stockList.get(stockList.size() - 1);
        assertThat(testStock.getTotalAmount()).isEqualByComparingTo(UPDATED_TOTAL_AMOUNT);
        assertThat(testStock.getTotalAmountSold()).isEqualByComparingTo(UPDATED_TOTAL_AMOUNT_SOLD);
        assertThat(testStock.getInitialQuantity()).isEqualTo(UPDATED_INITIAL_QUANTITY);
        assertThat(testStock.getRemainingQuantity()).isEqualTo(UPDATED_REMAINING_QUANTITY);
        assertThat(testStock.getStatus()).isEqualTo(UPDATED_STATUS);
    }

    @Test
    void putNonExistingStock() throws Exception {
        int databaseSizeBeforeUpdate = stockRepository.findAll().collectList().block().size();
        stock.setId(longCount.incrementAndGet());

        // Create the Stock
        StockDTO stockDTO = stockMapper.toDto(stock);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, stockDTO.getId())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(stockDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Stock in the database
        List<Stock> stockList = stockRepository.findAll().collectList().block();
        assertThat(stockList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void putWithIdMismatchStock() throws Exception {
        int databaseSizeBeforeUpdate = stockRepository.findAll().collectList().block().size();
        stock.setId(longCount.incrementAndGet());

        // Create the Stock
        StockDTO stockDTO = stockMapper.toDto(stock);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, longCount.incrementAndGet())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(stockDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Stock in the database
        List<Stock> stockList = stockRepository.findAll().collectList().block();
        assertThat(stockList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void putWithMissingIdPathParamStock() throws Exception {
        int databaseSizeBeforeUpdate = stockRepository.findAll().collectList().block().size();
        stock.setId(longCount.incrementAndGet());

        // Create the Stock
        StockDTO stockDTO = stockMapper.toDto(stock);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(stockDTO))
            .exchange()
            .expectStatus()
            .isEqualTo(405);

        // Validate the Stock in the database
        List<Stock> stockList = stockRepository.findAll().collectList().block();
        assertThat(stockList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void partialUpdateStockWithPatch() throws Exception {
        // Initialize the database
        stockRepository.save(stock).block();

        int databaseSizeBeforeUpdate = stockRepository.findAll().collectList().block().size();

        // Update the stock using partial update
        Stock partialUpdatedStock = new Stock();
        partialUpdatedStock.setId(stock.getId());

        partialUpdatedStock.totalAmount(UPDATED_TOTAL_AMOUNT).remainingQuantity(UPDATED_REMAINING_QUANTITY);

        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, partialUpdatedStock.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(partialUpdatedStock))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the Stock in the database
        List<Stock> stockList = stockRepository.findAll().collectList().block();
        assertThat(stockList).hasSize(databaseSizeBeforeUpdate);
        Stock testStock = stockList.get(stockList.size() - 1);
        assertThat(testStock.getTotalAmount()).isEqualByComparingTo(UPDATED_TOTAL_AMOUNT);
        assertThat(testStock.getTotalAmountSold()).isEqualByComparingTo(DEFAULT_TOTAL_AMOUNT_SOLD);
        assertThat(testStock.getInitialQuantity()).isEqualTo(DEFAULT_INITIAL_QUANTITY);
        assertThat(testStock.getRemainingQuantity()).isEqualTo(UPDATED_REMAINING_QUANTITY);
        assertThat(testStock.getStatus()).isEqualTo(DEFAULT_STATUS);
    }

    @Test
    void fullUpdateStockWithPatch() throws Exception {
        // Initialize the database
        stockRepository.save(stock).block();

        int databaseSizeBeforeUpdate = stockRepository.findAll().collectList().block().size();

        // Update the stock using partial update
        Stock partialUpdatedStock = new Stock();
        partialUpdatedStock.setId(stock.getId());

        partialUpdatedStock
            .totalAmount(UPDATED_TOTAL_AMOUNT)
            .totalAmountSold(UPDATED_TOTAL_AMOUNT_SOLD)
            .initialQuantity(UPDATED_INITIAL_QUANTITY)
            .remainingQuantity(UPDATED_REMAINING_QUANTITY)
            .status(UPDATED_STATUS);

        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, partialUpdatedStock.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(partialUpdatedStock))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the Stock in the database
        List<Stock> stockList = stockRepository.findAll().collectList().block();
        assertThat(stockList).hasSize(databaseSizeBeforeUpdate);
        Stock testStock = stockList.get(stockList.size() - 1);
        assertThat(testStock.getTotalAmount()).isEqualByComparingTo(UPDATED_TOTAL_AMOUNT);
        assertThat(testStock.getTotalAmountSold()).isEqualByComparingTo(UPDATED_TOTAL_AMOUNT_SOLD);
        assertThat(testStock.getInitialQuantity()).isEqualTo(UPDATED_INITIAL_QUANTITY);
        assertThat(testStock.getRemainingQuantity()).isEqualTo(UPDATED_REMAINING_QUANTITY);
        assertThat(testStock.getStatus()).isEqualTo(UPDATED_STATUS);
    }

    @Test
    void patchNonExistingStock() throws Exception {
        int databaseSizeBeforeUpdate = stockRepository.findAll().collectList().block().size();
        stock.setId(longCount.incrementAndGet());

        // Create the Stock
        StockDTO stockDTO = stockMapper.toDto(stock);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, stockDTO.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(stockDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Stock in the database
        List<Stock> stockList = stockRepository.findAll().collectList().block();
        assertThat(stockList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void patchWithIdMismatchStock() throws Exception {
        int databaseSizeBeforeUpdate = stockRepository.findAll().collectList().block().size();
        stock.setId(longCount.incrementAndGet());

        // Create the Stock
        StockDTO stockDTO = stockMapper.toDto(stock);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, longCount.incrementAndGet())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(stockDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Stock in the database
        List<Stock> stockList = stockRepository.findAll().collectList().block();
        assertThat(stockList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void patchWithMissingIdPathParamStock() throws Exception {
        int databaseSizeBeforeUpdate = stockRepository.findAll().collectList().block().size();
        stock.setId(longCount.incrementAndGet());

        // Create the Stock
        StockDTO stockDTO = stockMapper.toDto(stock);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(stockDTO))
            .exchange()
            .expectStatus()
            .isEqualTo(405);

        // Validate the Stock in the database
        List<Stock> stockList = stockRepository.findAll().collectList().block();
        assertThat(stockList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void deleteStock() {
        // Initialize the database
        stockRepository.save(stock).block();

        int databaseSizeBeforeDelete = stockRepository.findAll().collectList().block().size();

        // Delete the stock
        webTestClient
            .delete()
            .uri(ENTITY_API_URL_ID, stock.getId())
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isNoContent();

        // Validate the database contains one less item
        List<Stock> stockList = stockRepository.findAll().collectList().block();
        assertThat(stockList).hasSize(databaseSizeBeforeDelete - 1);
    }
}
