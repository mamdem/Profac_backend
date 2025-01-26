package com.profac.app.web.rest;

import static com.profac.app.web.rest.TestUtil.sameNumber;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.is;

import com.profac.app.IntegrationTest;
import com.profac.app.domain.Product;
import com.profac.app.domain.enumeration.ProductStatus;
import com.profac.app.repository.EntityManager;
import com.profac.app.repository.ProductRepository;
import com.profac.app.service.dto.ProductDTO;
import com.profac.app.service.mapper.ProductMapper;
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
 * Integration tests for the {@link ProductResource} REST controller.
 */
@IntegrationTest
@AutoConfigureWebTestClient(timeout = IntegrationTest.DEFAULT_ENTITY_TIMEOUT)
@WithMockUser
class ProductResourceIT {

    private static final Integer DEFAULT_PRODUCT_NUMBER = 1;
    private static final Integer UPDATED_PRODUCT_NUMBER = 2;

    private static final String DEFAULT_NAME = "AAAAAAAAAA";
    private static final String UPDATED_NAME = "BBBBBBBBBB";

    private static final BigDecimal DEFAULT_AMOUNT = new BigDecimal(1);
    private static final BigDecimal UPDATED_AMOUNT = new BigDecimal(2);

    private static final String DEFAULT_DESCRIPTION = "AAAAAAAAAA";
    private static final String UPDATED_DESCRIPTION = "BBBBBBBBBB";

    private static final ProductStatus DEFAULT_STATUS = ProductStatus.ACTIVE;
    private static final ProductStatus UPDATED_STATUS = ProductStatus.INACTIVE;

    private static final String ENTITY_API_URL = "/api/products";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static Random random = new Random();
    private static AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private ProductMapper productMapper;

    @Autowired
    private EntityManager em;

    @Autowired
    private WebTestClient webTestClient;

    private Product product;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Product createEntity(EntityManager em) {
        Product product = new Product()
            .productNumber(DEFAULT_PRODUCT_NUMBER)
            .name(DEFAULT_NAME)
            .amount(DEFAULT_AMOUNT)
            .description(DEFAULT_DESCRIPTION)
            .status(DEFAULT_STATUS);
        return product;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Product createUpdatedEntity(EntityManager em) {
        Product product = new Product()
            .productNumber(UPDATED_PRODUCT_NUMBER)
            .name(UPDATED_NAME)
            .amount(UPDATED_AMOUNT)
            .description(UPDATED_DESCRIPTION)
            .status(UPDATED_STATUS);
        return product;
    }

    public static void deleteEntities(EntityManager em) {
        try {
            em.deleteAll(Product.class).block();
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
        product = createEntity(em);
    }

    @Test
    void createProduct() throws Exception {
        int databaseSizeBeforeCreate = productRepository.findAll().collectList().block().size();
        // Create the Product
        ProductDTO productDTO = productMapper.toDto(product);
        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(productDTO))
            .exchange()
            .expectStatus()
            .isCreated();

        // Validate the Product in the database
        List<Product> productList = productRepository.findAll().collectList().block();
        assertThat(productList).hasSize(databaseSizeBeforeCreate + 1);
        Product testProduct = productList.get(productList.size() - 1);
        assertThat(testProduct.getProductNumber()).isEqualTo(DEFAULT_PRODUCT_NUMBER);
        assertThat(testProduct.getName()).isEqualTo(DEFAULT_NAME);
        assertThat(testProduct.getAmount()).isEqualByComparingTo(DEFAULT_AMOUNT);
        assertThat(testProduct.getDescription()).isEqualTo(DEFAULT_DESCRIPTION);
        assertThat(testProduct.getStatus()).isEqualTo(DEFAULT_STATUS);
    }

    @Test
    void createProductWithExistingId() throws Exception {
        // Create the Product with an existing ID
        product.setId(1L);
        ProductDTO productDTO = productMapper.toDto(product);

        int databaseSizeBeforeCreate = productRepository.findAll().collectList().block().size();

        // An entity with an existing ID cannot be created, so this API call must fail
        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(productDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Product in the database
        List<Product> productList = productRepository.findAll().collectList().block();
        assertThat(productList).hasSize(databaseSizeBeforeCreate);
    }

    @Test
    void checkProductNumberIsRequired() throws Exception {
        int databaseSizeBeforeTest = productRepository.findAll().collectList().block().size();
        // set the field null
        product.setProductNumber(null);

        // Create the Product, which fails.
        ProductDTO productDTO = productMapper.toDto(product);

        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(productDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        List<Product> productList = productRepository.findAll().collectList().block();
        assertThat(productList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    void checkNameIsRequired() throws Exception {
        int databaseSizeBeforeTest = productRepository.findAll().collectList().block().size();
        // set the field null
        product.setName(null);

        // Create the Product, which fails.
        ProductDTO productDTO = productMapper.toDto(product);

        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(productDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        List<Product> productList = productRepository.findAll().collectList().block();
        assertThat(productList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    void checkAmountIsRequired() throws Exception {
        int databaseSizeBeforeTest = productRepository.findAll().collectList().block().size();
        // set the field null
        product.setAmount(null);

        // Create the Product, which fails.
        ProductDTO productDTO = productMapper.toDto(product);

        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(productDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        List<Product> productList = productRepository.findAll().collectList().block();
        assertThat(productList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    void getAllProducts() {
        // Initialize the database
        productRepository.save(product).block();

        // Get all the productList
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
            .value(hasItem(product.getId().intValue()))
            .jsonPath("$.[*].productNumber")
            .value(hasItem(DEFAULT_PRODUCT_NUMBER))
            .jsonPath("$.[*].name")
            .value(hasItem(DEFAULT_NAME))
            .jsonPath("$.[*].amount")
            .value(hasItem(sameNumber(DEFAULT_AMOUNT)))
            .jsonPath("$.[*].description")
            .value(hasItem(DEFAULT_DESCRIPTION.toString()))
            .jsonPath("$.[*].status")
            .value(hasItem(DEFAULT_STATUS.toString()));
    }

    @Test
    void getProduct() {
        // Initialize the database
        productRepository.save(product).block();

        // Get the product
        webTestClient
            .get()
            .uri(ENTITY_API_URL_ID, product.getId())
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isOk()
            .expectHeader()
            .contentType(MediaType.APPLICATION_JSON)
            .expectBody()
            .jsonPath("$.id")
            .value(is(product.getId().intValue()))
            .jsonPath("$.productNumber")
            .value(is(DEFAULT_PRODUCT_NUMBER))
            .jsonPath("$.name")
            .value(is(DEFAULT_NAME))
            .jsonPath("$.amount")
            .value(is(sameNumber(DEFAULT_AMOUNT)))
            .jsonPath("$.description")
            .value(is(DEFAULT_DESCRIPTION.toString()))
            .jsonPath("$.status")
            .value(is(DEFAULT_STATUS.toString()));
    }

    @Test
    void getNonExistingProduct() {
        // Get the product
        webTestClient
            .get()
            .uri(ENTITY_API_URL_ID, Long.MAX_VALUE)
            .accept(MediaType.APPLICATION_PROBLEM_JSON)
            .exchange()
            .expectStatus()
            .isNotFound();
    }

    @Test
    void putExistingProduct() throws Exception {
        // Initialize the database
        productRepository.save(product).block();

        int databaseSizeBeforeUpdate = productRepository.findAll().collectList().block().size();

        // Update the product
        Product updatedProduct = productRepository.findById(product.getId()).block();
        updatedProduct
            .productNumber(UPDATED_PRODUCT_NUMBER)
            .name(UPDATED_NAME)
            .amount(UPDATED_AMOUNT)
            .description(UPDATED_DESCRIPTION)
            .status(UPDATED_STATUS);
        ProductDTO productDTO = productMapper.toDto(updatedProduct);

        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, productDTO.getId())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(productDTO))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the Product in the database
        List<Product> productList = productRepository.findAll().collectList().block();
        assertThat(productList).hasSize(databaseSizeBeforeUpdate);
        Product testProduct = productList.get(productList.size() - 1);
        assertThat(testProduct.getProductNumber()).isEqualTo(UPDATED_PRODUCT_NUMBER);
        assertThat(testProduct.getName()).isEqualTo(UPDATED_NAME);
        assertThat(testProduct.getAmount()).isEqualByComparingTo(UPDATED_AMOUNT);
        assertThat(testProduct.getDescription()).isEqualTo(UPDATED_DESCRIPTION);
        assertThat(testProduct.getStatus()).isEqualTo(UPDATED_STATUS);
    }

    @Test
    void putNonExistingProduct() throws Exception {
        int databaseSizeBeforeUpdate = productRepository.findAll().collectList().block().size();
        product.setId(longCount.incrementAndGet());

        // Create the Product
        ProductDTO productDTO = productMapper.toDto(product);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, productDTO.getId())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(productDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Product in the database
        List<Product> productList = productRepository.findAll().collectList().block();
        assertThat(productList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void putWithIdMismatchProduct() throws Exception {
        int databaseSizeBeforeUpdate = productRepository.findAll().collectList().block().size();
        product.setId(longCount.incrementAndGet());

        // Create the Product
        ProductDTO productDTO = productMapper.toDto(product);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, longCount.incrementAndGet())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(productDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Product in the database
        List<Product> productList = productRepository.findAll().collectList().block();
        assertThat(productList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void putWithMissingIdPathParamProduct() throws Exception {
        int databaseSizeBeforeUpdate = productRepository.findAll().collectList().block().size();
        product.setId(longCount.incrementAndGet());

        // Create the Product
        ProductDTO productDTO = productMapper.toDto(product);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(productDTO))
            .exchange()
            .expectStatus()
            .isEqualTo(405);

        // Validate the Product in the database
        List<Product> productList = productRepository.findAll().collectList().block();
        assertThat(productList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void partialUpdateProductWithPatch() throws Exception {
        // Initialize the database
        productRepository.save(product).block();

        int databaseSizeBeforeUpdate = productRepository.findAll().collectList().block().size();

        // Update the product using partial update
        Product partialUpdatedProduct = new Product();
        partialUpdatedProduct.setId(product.getId());

        partialUpdatedProduct.description(UPDATED_DESCRIPTION).status(UPDATED_STATUS);

        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, partialUpdatedProduct.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(partialUpdatedProduct))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the Product in the database
        List<Product> productList = productRepository.findAll().collectList().block();
        assertThat(productList).hasSize(databaseSizeBeforeUpdate);
        Product testProduct = productList.get(productList.size() - 1);
        assertThat(testProduct.getProductNumber()).isEqualTo(DEFAULT_PRODUCT_NUMBER);
        assertThat(testProduct.getName()).isEqualTo(DEFAULT_NAME);
        assertThat(testProduct.getAmount()).isEqualByComparingTo(DEFAULT_AMOUNT);
        assertThat(testProduct.getDescription()).isEqualTo(UPDATED_DESCRIPTION);
        assertThat(testProduct.getStatus()).isEqualTo(UPDATED_STATUS);
    }

    @Test
    void fullUpdateProductWithPatch() throws Exception {
        // Initialize the database
        productRepository.save(product).block();

        int databaseSizeBeforeUpdate = productRepository.findAll().collectList().block().size();

        // Update the product using partial update
        Product partialUpdatedProduct = new Product();
        partialUpdatedProduct.setId(product.getId());

        partialUpdatedProduct
            .productNumber(UPDATED_PRODUCT_NUMBER)
            .name(UPDATED_NAME)
            .amount(UPDATED_AMOUNT)
            .description(UPDATED_DESCRIPTION)
            .status(UPDATED_STATUS);

        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, partialUpdatedProduct.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(partialUpdatedProduct))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the Product in the database
        List<Product> productList = productRepository.findAll().collectList().block();
        assertThat(productList).hasSize(databaseSizeBeforeUpdate);
        Product testProduct = productList.get(productList.size() - 1);
        assertThat(testProduct.getProductNumber()).isEqualTo(UPDATED_PRODUCT_NUMBER);
        assertThat(testProduct.getName()).isEqualTo(UPDATED_NAME);
        assertThat(testProduct.getAmount()).isEqualByComparingTo(UPDATED_AMOUNT);
        assertThat(testProduct.getDescription()).isEqualTo(UPDATED_DESCRIPTION);
        assertThat(testProduct.getStatus()).isEqualTo(UPDATED_STATUS);
    }

    @Test
    void patchNonExistingProduct() throws Exception {
        int databaseSizeBeforeUpdate = productRepository.findAll().collectList().block().size();
        product.setId(longCount.incrementAndGet());

        // Create the Product
        ProductDTO productDTO = productMapper.toDto(product);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, productDTO.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(productDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Product in the database
        List<Product> productList = productRepository.findAll().collectList().block();
        assertThat(productList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void patchWithIdMismatchProduct() throws Exception {
        int databaseSizeBeforeUpdate = productRepository.findAll().collectList().block().size();
        product.setId(longCount.incrementAndGet());

        // Create the Product
        ProductDTO productDTO = productMapper.toDto(product);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, longCount.incrementAndGet())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(productDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Product in the database
        List<Product> productList = productRepository.findAll().collectList().block();
        assertThat(productList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void patchWithMissingIdPathParamProduct() throws Exception {
        int databaseSizeBeforeUpdate = productRepository.findAll().collectList().block().size();
        product.setId(longCount.incrementAndGet());

        // Create the Product
        ProductDTO productDTO = productMapper.toDto(product);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(productDTO))
            .exchange()
            .expectStatus()
            .isEqualTo(405);

        // Validate the Product in the database
        List<Product> productList = productRepository.findAll().collectList().block();
        assertThat(productList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void deleteProduct() {
        // Initialize the database
        productRepository.save(product).block();

        int databaseSizeBeforeDelete = productRepository.findAll().collectList().block().size();

        // Delete the product
        webTestClient
            .delete()
            .uri(ENTITY_API_URL_ID, product.getId())
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isNoContent();

        // Validate the database contains one less item
        List<Product> productList = productRepository.findAll().collectList().block();
        assertThat(productList).hasSize(databaseSizeBeforeDelete - 1);
    }
}
