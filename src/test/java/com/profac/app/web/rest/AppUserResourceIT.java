package com.profac.app.web.rest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.is;

import com.profac.app.IntegrationTest;
import com.profac.app.domain.AppUser;
import com.profac.app.domain.enumeration.UserType;
import com.profac.app.domain.enumeration.appUserStatus;
import com.profac.app.repository.AppUserRepository;
import com.profac.app.repository.EntityManager;
import com.profac.app.service.dto.AppUserDTO;
import com.profac.app.service.mapper.AppUserMapper;
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
 * Integration tests for the {@link AppUserResource} REST controller.
 */
@IntegrationTest
@AutoConfigureWebTestClient(timeout = IntegrationTest.DEFAULT_ENTITY_TIMEOUT)
@WithMockUser
class AppUserResourceIT {

    private static final String DEFAULT_FIRST_NAME = "AAAAAAAAAA";
    private static final String UPDATED_FIRST_NAME = "BBBBBBBBBB";

    private static final String DEFAULT_LAST_NAME = "AAAAAAAAAA";
    private static final String UPDATED_LAST_NAME = "BBBBBBBBBB";

    private static final String DEFAULT_PASSWORD = "AAAAAAAAAA";
    private static final String UPDATED_PASSWORD = "BBBBBBBBBB";

    private static final String DEFAULT_PHONE_NUMBER = "AAAAAAAAAA";
    private static final String UPDATED_PHONE_NUMBER = "BBBBBBBBBB";

    private static final String DEFAULT_ADDRESS = "AAAAAAAAAA";
    private static final String UPDATED_ADDRESS = "BBBBBBBBBB";

    private static final UserType DEFAULT_USER_TYPE = UserType.SELLER;
    private static final UserType UPDATED_USER_TYPE = UserType.CASHIER;

    private static final appUserStatus DEFAULT_STATUS = appUserStatus.ACTIVE;
    private static final appUserStatus UPDATED_STATUS = appUserStatus.INACTIVE;

    private static final String ENTITY_API_URL = "/api/app-users";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static Random random = new Random();
    private static AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private AppUserRepository appUserRepository;

    @Autowired
    private AppUserMapper appUserMapper;

    @Autowired
    private EntityManager em;

    @Autowired
    private WebTestClient webTestClient;

    private AppUser appUser;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static AppUser createEntity(EntityManager em) {
        AppUser appUser = new AppUser()
            .firstName(DEFAULT_FIRST_NAME)
            .lastName(DEFAULT_LAST_NAME)
            .password(DEFAULT_PASSWORD)
            .phoneNumber(DEFAULT_PHONE_NUMBER)
            .address(DEFAULT_ADDRESS)
            .userType(DEFAULT_USER_TYPE)
            .status(DEFAULT_STATUS);
        return appUser;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static AppUser createUpdatedEntity(EntityManager em) {
        AppUser appUser = new AppUser()
            .firstName(UPDATED_FIRST_NAME)
            .lastName(UPDATED_LAST_NAME)
            .password(UPDATED_PASSWORD)
            .phoneNumber(UPDATED_PHONE_NUMBER)
            .address(UPDATED_ADDRESS)
            .userType(UPDATED_USER_TYPE)
            .status(UPDATED_STATUS);
        return appUser;
    }

    public static void deleteEntities(EntityManager em) {
        try {
            em.deleteAll(AppUser.class).block();
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
        appUser = createEntity(em);
    }

    @Test
    void createAppUser() throws Exception {
        int databaseSizeBeforeCreate = appUserRepository.findAll().collectList().block().size();
        // Create the AppUser
        AppUserDTO appUserDTO = appUserMapper.toDto(appUser);
        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(appUserDTO))
            .exchange()
            .expectStatus()
            .isCreated();

        // Validate the AppUser in the database
        List<AppUser> appUserList = appUserRepository.findAll().collectList().block();
        assertThat(appUserList).hasSize(databaseSizeBeforeCreate + 1);
        AppUser testAppUser = appUserList.get(appUserList.size() - 1);
        assertThat(testAppUser.getFirstName()).isEqualTo(DEFAULT_FIRST_NAME);
        assertThat(testAppUser.getLastName()).isEqualTo(DEFAULT_LAST_NAME);
        assertThat(testAppUser.getPassword()).isEqualTo(DEFAULT_PASSWORD);
        assertThat(testAppUser.getPhoneNumber()).isEqualTo(DEFAULT_PHONE_NUMBER);
        assertThat(testAppUser.getAddress()).isEqualTo(DEFAULT_ADDRESS);
        assertThat(testAppUser.getUserType()).isEqualTo(DEFAULT_USER_TYPE);
        assertThat(testAppUser.getStatus()).isEqualTo(DEFAULT_STATUS);
    }

    @Test
    void createAppUserWithExistingId() throws Exception {
        // Create the AppUser with an existing ID
        appUser.setId(1L);
        AppUserDTO appUserDTO = appUserMapper.toDto(appUser);

        int databaseSizeBeforeCreate = appUserRepository.findAll().collectList().block().size();

        // An entity with an existing ID cannot be created, so this API call must fail
        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(appUserDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the AppUser in the database
        List<AppUser> appUserList = appUserRepository.findAll().collectList().block();
        assertThat(appUserList).hasSize(databaseSizeBeforeCreate);
    }

    @Test
    void checkPhoneNumberIsRequired() throws Exception {
        int databaseSizeBeforeTest = appUserRepository.findAll().collectList().block().size();
        // set the field null
        appUser.setPhoneNumber(null);

        // Create the AppUser, which fails.
        AppUserDTO appUserDTO = appUserMapper.toDto(appUser);

        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(appUserDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        List<AppUser> appUserList = appUserRepository.findAll().collectList().block();
        assertThat(appUserList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    void getAllAppUsers() {
        // Initialize the database
        appUserRepository.save(appUser).block();

        // Get all the appUserList
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
            .value(hasItem(appUser.getId().intValue()))
            .jsonPath("$.[*].firstName")
            .value(hasItem(DEFAULT_FIRST_NAME))
            .jsonPath("$.[*].lastName")
            .value(hasItem(DEFAULT_LAST_NAME))
            .jsonPath("$.[*].password")
            .value(hasItem(DEFAULT_PASSWORD))
            .jsonPath("$.[*].phoneNumber")
            .value(hasItem(DEFAULT_PHONE_NUMBER))
            .jsonPath("$.[*].address")
            .value(hasItem(DEFAULT_ADDRESS))
            .jsonPath("$.[*].userType")
            .value(hasItem(DEFAULT_USER_TYPE.toString()))
            .jsonPath("$.[*].status")
            .value(hasItem(DEFAULT_STATUS.toString()));
    }

    @Test
    void getAppUser() {
        // Initialize the database
        appUserRepository.save(appUser).block();

        // Get the appUser
        webTestClient
            .get()
            .uri(ENTITY_API_URL_ID, appUser.getId())
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isOk()
            .expectHeader()
            .contentType(MediaType.APPLICATION_JSON)
            .expectBody()
            .jsonPath("$.id")
            .value(is(appUser.getId().intValue()))
            .jsonPath("$.firstName")
            .value(is(DEFAULT_FIRST_NAME))
            .jsonPath("$.lastName")
            .value(is(DEFAULT_LAST_NAME))
            .jsonPath("$.password")
            .value(is(DEFAULT_PASSWORD))
            .jsonPath("$.phoneNumber")
            .value(is(DEFAULT_PHONE_NUMBER))
            .jsonPath("$.address")
            .value(is(DEFAULT_ADDRESS))
            .jsonPath("$.userType")
            .value(is(DEFAULT_USER_TYPE.toString()))
            .jsonPath("$.status")
            .value(is(DEFAULT_STATUS.toString()));
    }

    @Test
    void getNonExistingAppUser() {
        // Get the appUser
        webTestClient
            .get()
            .uri(ENTITY_API_URL_ID, Long.MAX_VALUE)
            .accept(MediaType.APPLICATION_PROBLEM_JSON)
            .exchange()
            .expectStatus()
            .isNotFound();
    }

    @Test
    void putExistingAppUser() throws Exception {
        // Initialize the database
        appUserRepository.save(appUser).block();

        int databaseSizeBeforeUpdate = appUserRepository.findAll().collectList().block().size();

        // Update the appUser
        AppUser updatedAppUser = appUserRepository.findById(appUser.getId()).block();
        updatedAppUser
            .firstName(UPDATED_FIRST_NAME)
            .lastName(UPDATED_LAST_NAME)
            .password(UPDATED_PASSWORD)
            .phoneNumber(UPDATED_PHONE_NUMBER)
            .address(UPDATED_ADDRESS)
            .userType(UPDATED_USER_TYPE)
            .status(UPDATED_STATUS);
        AppUserDTO appUserDTO = appUserMapper.toDto(updatedAppUser);

        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, appUserDTO.getId())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(appUserDTO))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the AppUser in the database
        List<AppUser> appUserList = appUserRepository.findAll().collectList().block();
        assertThat(appUserList).hasSize(databaseSizeBeforeUpdate);
        AppUser testAppUser = appUserList.get(appUserList.size() - 1);
        assertThat(testAppUser.getFirstName()).isEqualTo(UPDATED_FIRST_NAME);
        assertThat(testAppUser.getLastName()).isEqualTo(UPDATED_LAST_NAME);
        assertThat(testAppUser.getPassword()).isEqualTo(UPDATED_PASSWORD);
        assertThat(testAppUser.getPhoneNumber()).isEqualTo(UPDATED_PHONE_NUMBER);
        assertThat(testAppUser.getAddress()).isEqualTo(UPDATED_ADDRESS);
        assertThat(testAppUser.getUserType()).isEqualTo(UPDATED_USER_TYPE);
        assertThat(testAppUser.getStatus()).isEqualTo(UPDATED_STATUS);
    }

    @Test
    void putNonExistingAppUser() throws Exception {
        int databaseSizeBeforeUpdate = appUserRepository.findAll().collectList().block().size();
        appUser.setId(longCount.incrementAndGet());

        // Create the AppUser
        AppUserDTO appUserDTO = appUserMapper.toDto(appUser);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, appUserDTO.getId())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(appUserDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the AppUser in the database
        List<AppUser> appUserList = appUserRepository.findAll().collectList().block();
        assertThat(appUserList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void putWithIdMismatchAppUser() throws Exception {
        int databaseSizeBeforeUpdate = appUserRepository.findAll().collectList().block().size();
        appUser.setId(longCount.incrementAndGet());

        // Create the AppUser
        AppUserDTO appUserDTO = appUserMapper.toDto(appUser);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, longCount.incrementAndGet())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(appUserDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the AppUser in the database
        List<AppUser> appUserList = appUserRepository.findAll().collectList().block();
        assertThat(appUserList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void putWithMissingIdPathParamAppUser() throws Exception {
        int databaseSizeBeforeUpdate = appUserRepository.findAll().collectList().block().size();
        appUser.setId(longCount.incrementAndGet());

        // Create the AppUser
        AppUserDTO appUserDTO = appUserMapper.toDto(appUser);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(appUserDTO))
            .exchange()
            .expectStatus()
            .isEqualTo(405);

        // Validate the AppUser in the database
        List<AppUser> appUserList = appUserRepository.findAll().collectList().block();
        assertThat(appUserList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void partialUpdateAppUserWithPatch() throws Exception {
        // Initialize the database
        appUserRepository.save(appUser).block();

        int databaseSizeBeforeUpdate = appUserRepository.findAll().collectList().block().size();

        // Update the appUser using partial update
        AppUser partialUpdatedAppUser = new AppUser();
        partialUpdatedAppUser.setId(appUser.getId());

        partialUpdatedAppUser
            .firstName(UPDATED_FIRST_NAME)
            .lastName(UPDATED_LAST_NAME)
            .password(UPDATED_PASSWORD)
            .phoneNumber(UPDATED_PHONE_NUMBER);

        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, partialUpdatedAppUser.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(partialUpdatedAppUser))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the AppUser in the database
        List<AppUser> appUserList = appUserRepository.findAll().collectList().block();
        assertThat(appUserList).hasSize(databaseSizeBeforeUpdate);
        AppUser testAppUser = appUserList.get(appUserList.size() - 1);
        assertThat(testAppUser.getFirstName()).isEqualTo(UPDATED_FIRST_NAME);
        assertThat(testAppUser.getLastName()).isEqualTo(UPDATED_LAST_NAME);
        assertThat(testAppUser.getPassword()).isEqualTo(UPDATED_PASSWORD);
        assertThat(testAppUser.getPhoneNumber()).isEqualTo(UPDATED_PHONE_NUMBER);
        assertThat(testAppUser.getAddress()).isEqualTo(DEFAULT_ADDRESS);
        assertThat(testAppUser.getUserType()).isEqualTo(DEFAULT_USER_TYPE);
        assertThat(testAppUser.getStatus()).isEqualTo(DEFAULT_STATUS);
    }

    @Test
    void fullUpdateAppUserWithPatch() throws Exception {
        // Initialize the database
        appUserRepository.save(appUser).block();

        int databaseSizeBeforeUpdate = appUserRepository.findAll().collectList().block().size();

        // Update the appUser using partial update
        AppUser partialUpdatedAppUser = new AppUser();
        partialUpdatedAppUser.setId(appUser.getId());

        partialUpdatedAppUser
            .firstName(UPDATED_FIRST_NAME)
            .lastName(UPDATED_LAST_NAME)
            .password(UPDATED_PASSWORD)
            .phoneNumber(UPDATED_PHONE_NUMBER)
            .address(UPDATED_ADDRESS)
            .userType(UPDATED_USER_TYPE)
            .status(UPDATED_STATUS);

        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, partialUpdatedAppUser.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(partialUpdatedAppUser))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the AppUser in the database
        List<AppUser> appUserList = appUserRepository.findAll().collectList().block();
        assertThat(appUserList).hasSize(databaseSizeBeforeUpdate);
        AppUser testAppUser = appUserList.get(appUserList.size() - 1);
        assertThat(testAppUser.getFirstName()).isEqualTo(UPDATED_FIRST_NAME);
        assertThat(testAppUser.getLastName()).isEqualTo(UPDATED_LAST_NAME);
        assertThat(testAppUser.getPassword()).isEqualTo(UPDATED_PASSWORD);
        assertThat(testAppUser.getPhoneNumber()).isEqualTo(UPDATED_PHONE_NUMBER);
        assertThat(testAppUser.getAddress()).isEqualTo(UPDATED_ADDRESS);
        assertThat(testAppUser.getUserType()).isEqualTo(UPDATED_USER_TYPE);
        assertThat(testAppUser.getStatus()).isEqualTo(UPDATED_STATUS);
    }

    @Test
    void patchNonExistingAppUser() throws Exception {
        int databaseSizeBeforeUpdate = appUserRepository.findAll().collectList().block().size();
        appUser.setId(longCount.incrementAndGet());

        // Create the AppUser
        AppUserDTO appUserDTO = appUserMapper.toDto(appUser);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, appUserDTO.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(appUserDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the AppUser in the database
        List<AppUser> appUserList = appUserRepository.findAll().collectList().block();
        assertThat(appUserList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void patchWithIdMismatchAppUser() throws Exception {
        int databaseSizeBeforeUpdate = appUserRepository.findAll().collectList().block().size();
        appUser.setId(longCount.incrementAndGet());

        // Create the AppUser
        AppUserDTO appUserDTO = appUserMapper.toDto(appUser);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, longCount.incrementAndGet())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(appUserDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the AppUser in the database
        List<AppUser> appUserList = appUserRepository.findAll().collectList().block();
        assertThat(appUserList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void patchWithMissingIdPathParamAppUser() throws Exception {
        int databaseSizeBeforeUpdate = appUserRepository.findAll().collectList().block().size();
        appUser.setId(longCount.incrementAndGet());

        // Create the AppUser
        AppUserDTO appUserDTO = appUserMapper.toDto(appUser);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(appUserDTO))
            .exchange()
            .expectStatus()
            .isEqualTo(405);

        // Validate the AppUser in the database
        List<AppUser> appUserList = appUserRepository.findAll().collectList().block();
        assertThat(appUserList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void deleteAppUser() {
        // Initialize the database
        appUserRepository.save(appUser).block();

        int databaseSizeBeforeDelete = appUserRepository.findAll().collectList().block().size();

        // Delete the appUser
        webTestClient
            .delete()
            .uri(ENTITY_API_URL_ID, appUser.getId())
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isNoContent();

        // Validate the database contains one less item
        List<AppUser> appUserList = appUserRepository.findAll().collectList().block();
        assertThat(appUserList).hasSize(databaseSizeBeforeDelete - 1);
    }
}
