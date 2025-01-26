package com.profac.app.web.rest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.is;

import com.profac.app.IntegrationTest;
import com.profac.app.domain.Image;
import com.profac.app.domain.enumeration.ImageStatus;
import com.profac.app.repository.EntityManager;
import com.profac.app.repository.ImageRepository;
import com.profac.app.service.dto.ImageDTO;
import com.profac.app.service.mapper.ImageMapper;
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
 * Integration tests for the {@link ImageResource} REST controller.
 */
@IntegrationTest
@AutoConfigureWebTestClient(timeout = IntegrationTest.DEFAULT_ENTITY_TIMEOUT)
@WithMockUser
class ImageResourceIT {

    private static final String DEFAULT_URL = "AAAAAAAAAA";
    private static final String UPDATED_URL = "BBBBBBBBBB";

    private static final ImageStatus DEFAULT_STATUS = ImageStatus.ACTIVE;
    private static final ImageStatus UPDATED_STATUS = ImageStatus.INACTIVE;

    private static final String ENTITY_API_URL = "/api/images";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static Random random = new Random();
    private static AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private ImageRepository imageRepository;

    @Autowired
    private ImageMapper imageMapper;

    @Autowired
    private EntityManager em;

    @Autowired
    private WebTestClient webTestClient;

    private Image image;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Image createEntity(EntityManager em) {
        Image image = new Image().url(DEFAULT_URL).status(DEFAULT_STATUS);
        return image;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Image createUpdatedEntity(EntityManager em) {
        Image image = new Image().url(UPDATED_URL).status(UPDATED_STATUS);
        return image;
    }

    public static void deleteEntities(EntityManager em) {
        try {
            em.deleteAll(Image.class).block();
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
        image = createEntity(em);
    }

    @Test
    void createImage() throws Exception {
        int databaseSizeBeforeCreate = imageRepository.findAll().collectList().block().size();
        // Create the Image
        ImageDTO imageDTO = imageMapper.toDto(image);
        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(imageDTO))
            .exchange()
            .expectStatus()
            .isCreated();

        // Validate the Image in the database
        List<Image> imageList = imageRepository.findAll().collectList().block();
        assertThat(imageList).hasSize(databaseSizeBeforeCreate + 1);
        Image testImage = imageList.get(imageList.size() - 1);
        assertThat(testImage.getUrl()).isEqualTo(DEFAULT_URL);
        assertThat(testImage.getStatus()).isEqualTo(DEFAULT_STATUS);
    }

    @Test
    void createImageWithExistingId() throws Exception {
        // Create the Image with an existing ID
        image.setId(1L);
        ImageDTO imageDTO = imageMapper.toDto(image);

        int databaseSizeBeforeCreate = imageRepository.findAll().collectList().block().size();

        // An entity with an existing ID cannot be created, so this API call must fail
        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(imageDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Image in the database
        List<Image> imageList = imageRepository.findAll().collectList().block();
        assertThat(imageList).hasSize(databaseSizeBeforeCreate);
    }

    @Test
    void getAllImages() {
        // Initialize the database
        imageRepository.save(image).block();

        // Get all the imageList
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
            .value(hasItem(image.getId().intValue()))
            .jsonPath("$.[*].url")
            .value(hasItem(DEFAULT_URL.toString()))
            .jsonPath("$.[*].status")
            .value(hasItem(DEFAULT_STATUS.toString()));
    }

    @Test
    void getImage() {
        // Initialize the database
        imageRepository.save(image).block();

        // Get the image
        webTestClient
            .get()
            .uri(ENTITY_API_URL_ID, image.getId())
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isOk()
            .expectHeader()
            .contentType(MediaType.APPLICATION_JSON)
            .expectBody()
            .jsonPath("$.id")
            .value(is(image.getId().intValue()))
            .jsonPath("$.url")
            .value(is(DEFAULT_URL.toString()))
            .jsonPath("$.status")
            .value(is(DEFAULT_STATUS.toString()));
    }

    @Test
    void getNonExistingImage() {
        // Get the image
        webTestClient
            .get()
            .uri(ENTITY_API_URL_ID, Long.MAX_VALUE)
            .accept(MediaType.APPLICATION_PROBLEM_JSON)
            .exchange()
            .expectStatus()
            .isNotFound();
    }

    @Test
    void putExistingImage() throws Exception {
        // Initialize the database
        imageRepository.save(image).block();

        int databaseSizeBeforeUpdate = imageRepository.findAll().collectList().block().size();

        // Update the image
        Image updatedImage = imageRepository.findById(image.getId()).block();
        updatedImage.url(UPDATED_URL).status(UPDATED_STATUS);
        ImageDTO imageDTO = imageMapper.toDto(updatedImage);

        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, imageDTO.getId())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(imageDTO))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the Image in the database
        List<Image> imageList = imageRepository.findAll().collectList().block();
        assertThat(imageList).hasSize(databaseSizeBeforeUpdate);
        Image testImage = imageList.get(imageList.size() - 1);
        assertThat(testImage.getUrl()).isEqualTo(UPDATED_URL);
        assertThat(testImage.getStatus()).isEqualTo(UPDATED_STATUS);
    }

    @Test
    void putNonExistingImage() throws Exception {
        int databaseSizeBeforeUpdate = imageRepository.findAll().collectList().block().size();
        image.setId(longCount.incrementAndGet());

        // Create the Image
        ImageDTO imageDTO = imageMapper.toDto(image);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, imageDTO.getId())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(imageDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Image in the database
        List<Image> imageList = imageRepository.findAll().collectList().block();
        assertThat(imageList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void putWithIdMismatchImage() throws Exception {
        int databaseSizeBeforeUpdate = imageRepository.findAll().collectList().block().size();
        image.setId(longCount.incrementAndGet());

        // Create the Image
        ImageDTO imageDTO = imageMapper.toDto(image);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, longCount.incrementAndGet())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(imageDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Image in the database
        List<Image> imageList = imageRepository.findAll().collectList().block();
        assertThat(imageList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void putWithMissingIdPathParamImage() throws Exception {
        int databaseSizeBeforeUpdate = imageRepository.findAll().collectList().block().size();
        image.setId(longCount.incrementAndGet());

        // Create the Image
        ImageDTO imageDTO = imageMapper.toDto(image);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(imageDTO))
            .exchange()
            .expectStatus()
            .isEqualTo(405);

        // Validate the Image in the database
        List<Image> imageList = imageRepository.findAll().collectList().block();
        assertThat(imageList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void partialUpdateImageWithPatch() throws Exception {
        // Initialize the database
        imageRepository.save(image).block();

        int databaseSizeBeforeUpdate = imageRepository.findAll().collectList().block().size();

        // Update the image using partial update
        Image partialUpdatedImage = new Image();
        partialUpdatedImage.setId(image.getId());

        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, partialUpdatedImage.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(partialUpdatedImage))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the Image in the database
        List<Image> imageList = imageRepository.findAll().collectList().block();
        assertThat(imageList).hasSize(databaseSizeBeforeUpdate);
        Image testImage = imageList.get(imageList.size() - 1);
        assertThat(testImage.getUrl()).isEqualTo(DEFAULT_URL);
        assertThat(testImage.getStatus()).isEqualTo(DEFAULT_STATUS);
    }

    @Test
    void fullUpdateImageWithPatch() throws Exception {
        // Initialize the database
        imageRepository.save(image).block();

        int databaseSizeBeforeUpdate = imageRepository.findAll().collectList().block().size();

        // Update the image using partial update
        Image partialUpdatedImage = new Image();
        partialUpdatedImage.setId(image.getId());

        partialUpdatedImage.url(UPDATED_URL).status(UPDATED_STATUS);

        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, partialUpdatedImage.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(partialUpdatedImage))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the Image in the database
        List<Image> imageList = imageRepository.findAll().collectList().block();
        assertThat(imageList).hasSize(databaseSizeBeforeUpdate);
        Image testImage = imageList.get(imageList.size() - 1);
        assertThat(testImage.getUrl()).isEqualTo(UPDATED_URL);
        assertThat(testImage.getStatus()).isEqualTo(UPDATED_STATUS);
    }

    @Test
    void patchNonExistingImage() throws Exception {
        int databaseSizeBeforeUpdate = imageRepository.findAll().collectList().block().size();
        image.setId(longCount.incrementAndGet());

        // Create the Image
        ImageDTO imageDTO = imageMapper.toDto(image);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, imageDTO.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(imageDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Image in the database
        List<Image> imageList = imageRepository.findAll().collectList().block();
        assertThat(imageList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void patchWithIdMismatchImage() throws Exception {
        int databaseSizeBeforeUpdate = imageRepository.findAll().collectList().block().size();
        image.setId(longCount.incrementAndGet());

        // Create the Image
        ImageDTO imageDTO = imageMapper.toDto(image);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, longCount.incrementAndGet())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(imageDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Image in the database
        List<Image> imageList = imageRepository.findAll().collectList().block();
        assertThat(imageList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void patchWithMissingIdPathParamImage() throws Exception {
        int databaseSizeBeforeUpdate = imageRepository.findAll().collectList().block().size();
        image.setId(longCount.incrementAndGet());

        // Create the Image
        ImageDTO imageDTO = imageMapper.toDto(image);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(imageDTO))
            .exchange()
            .expectStatus()
            .isEqualTo(405);

        // Validate the Image in the database
        List<Image> imageList = imageRepository.findAll().collectList().block();
        assertThat(imageList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void deleteImage() {
        // Initialize the database
        imageRepository.save(image).block();

        int databaseSizeBeforeDelete = imageRepository.findAll().collectList().block().size();

        // Delete the image
        webTestClient
            .delete()
            .uri(ENTITY_API_URL_ID, image.getId())
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isNoContent();

        // Validate the database contains one less item
        List<Image> imageList = imageRepository.findAll().collectList().block();
        assertThat(imageList).hasSize(databaseSizeBeforeDelete - 1);
    }
}
