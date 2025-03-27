package com.profac.app.web.rest;

import com.profac.app.repository.InvoiceProductRepository;
import com.profac.app.service.InvoiceProductService;
import com.profac.app.service.dto.InvoiceProductDTO;
import com.profac.app.utils.exception.BusinessBadRequestException;
import com.profac.app.web.rest.errors.BadRequestAlertException;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Objects;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.util.UriComponentsBuilder;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import tech.jhipster.web.util.HeaderUtil;
import tech.jhipster.web.util.PaginationUtil;
import tech.jhipster.web.util.reactive.ResponseUtil;

/**
 * REST controller for managing {@link com.profac.app.domain.InvoiceProduct}.
 */
@RestController
@RequestMapping("/api/invoice-products")
public class InvoiceProductResource {

    private final Logger log = LoggerFactory.getLogger(InvoiceProductResource.class);

    private static final String ENTITY_NAME = "invoiceProduct";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final InvoiceProductService invoiceProductService;

    private final InvoiceProductRepository invoiceProductRepository;

    public InvoiceProductResource(InvoiceProductService invoiceProductService, InvoiceProductRepository invoiceProductRepository) {
        this.invoiceProductService = invoiceProductService;
        this.invoiceProductRepository = invoiceProductRepository;
    }

    /**
     * {@code POST  /invoice-products} : Create a new invoiceProduct.
     *
     * @param invoiceProductDTO the invoiceProductDTO to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new invoiceProductDTO, or with status {@code 400 (Bad Request)} if the invoiceProduct has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("")
    public Mono<ResponseEntity<InvoiceProductDTO>> createInvoiceProduct(@Valid @RequestBody InvoiceProductDTO invoiceProductDTO)
  {
       try{ log.debug("REST request to save InvoiceProduct : {}", invoiceProductDTO);
        if (invoiceProductDTO.getId() != null) {
            throw new BadRequestAlertException("A new invoiceProduct cannot already have an ID", ENTITY_NAME, "idexists");
        }
        return invoiceProductService
            .save(invoiceProductDTO)
            .map(result -> {
                try {
                    return ResponseEntity
                        .created(new URI("/api/invoice-products/" + result.getId()))
                        .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, result.getId().toString()))
                        .body(result);
                } catch (URISyntaxException e) {
                    throw new RuntimeException(e);
                }
            });
    } catch (Exception e) {
        log.error("Une erreur s'est produite: {}", e.getMessage());
        throw new BusinessBadRequestException("Une erreur s'est produite");
    }
    }

    /**
     * {@code PUT  /invoice-products/:id} : Updates an existing invoiceProduct.
     *
     * @param id the id of the invoiceProductDTO to save.
     * @param invoiceProductDTO the invoiceProductDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated invoiceProductDTO,
     * or with status {@code 400 (Bad Request)} if the invoiceProductDTO is not valid,
     * or with status {@code 500 (Internal Server Error)} if the invoiceProductDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/{id}")
    public Mono<ResponseEntity<InvoiceProductDTO>> updateInvoiceProduct(
        @PathVariable(value = "id", required = false) final Long id,
        @Valid @RequestBody InvoiceProductDTO invoiceProductDTO
    ) throws URISyntaxException {
        try{
            log.debug("REST request to update InvoiceProduct : {}, {}", id, invoiceProductDTO);

        if (invoiceProductDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, invoiceProductDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        return invoiceProductRepository
            .existsById(id)
            .flatMap(exists -> {
                if (!exists) {
                    return Mono.error(new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound"));
                }

                return invoiceProductService
                    .update(invoiceProductDTO)
                    .switchIfEmpty(Mono.error(new ResponseStatusException(HttpStatus.NOT_FOUND)))
                    .map(result ->
                        ResponseEntity
                            .ok()
                            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, result.getId().toString()))
                            .body(result)
                    );
            });
        } catch (Exception e) {
            log.error("Une erreur s'est produite: {}", e.getMessage());
            throw new BusinessBadRequestException("Une erreur s'est produite");
        }
    }

    /**
     * {@code PATCH  /invoice-products/:id} : Partial updates given fields of an existing invoiceProduct, field will ignore if it is null
     *
     * @param id the id of the invoiceProductDTO to save.
     * @param invoiceProductDTO the invoiceProductDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated invoiceProductDTO,
     * or with status {@code 400 (Bad Request)} if the invoiceProductDTO is not valid,
     * or with status {@code 404 (Not Found)} if the invoiceProductDTO is not found,
     * or with status {@code 500 (Internal Server Error)} if the invoiceProductDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public Mono<ResponseEntity<InvoiceProductDTO>> partialUpdateInvoiceProduct(
        @PathVariable(value = "id", required = false) final Long id,
        @NotNull @RequestBody InvoiceProductDTO invoiceProductDTO
    ) throws URISyntaxException {
       try {
           log.debug("REST request to partial update InvoiceProduct partially : {}, {}", id, invoiceProductDTO);

        if (invoiceProductDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, invoiceProductDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        return invoiceProductRepository
            .existsById(id)
            .flatMap(exists -> {
                if (!exists) {
                    return Mono.error(new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound"));
                }

                Mono<InvoiceProductDTO> result = invoiceProductService.partialUpdate(invoiceProductDTO);

                return result
                    .switchIfEmpty(Mono.error(new ResponseStatusException(HttpStatus.NOT_FOUND)))
                    .map(res ->
                        ResponseEntity
                            .ok()
                            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, res.getId().toString()))
                            .body(res)
                    );
            });
       } catch (Exception e) {
           log.error("Une erreur s'est produite: {}", e.getMessage());
           throw new BusinessBadRequestException("Une erreur s'est produite");
       }
    }

    /**
     * {@code GET  /invoice-products} : get all the invoiceProducts.
     *
     * @param pageable the pagination information.
     * @param request a {@link ServerHttpRequest} request.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of invoiceProducts in body.
     */
    @GetMapping(value = "", produces = MediaType.APPLICATION_JSON_VALUE)
    public Mono<ResponseEntity<List<InvoiceProductDTO>>> getAllInvoiceProducts(
        @org.springdoc.core.annotations.ParameterObject Pageable pageable,
        ServerHttpRequest request
    ) {
      try {
          log.debug("REST request to get a page of InvoiceProducts");

        return invoiceProductService
            .countAll()
            .zipWith(invoiceProductService.findAll(pageable).collectList())
            .map(countWithEntities ->
                ResponseEntity
                    .ok()
                    .headers(
                        PaginationUtil.generatePaginationHttpHeaders(
                            UriComponentsBuilder.fromHttpRequest(request),
                            new PageImpl<>(countWithEntities.getT2(), pageable, countWithEntities.getT1())
                        )
                    )
                    .body(countWithEntities.getT2())
            );
      } catch (Exception e) {
          log.error("Une erreur s'est produite: {}", e.getMessage());
          throw new BusinessBadRequestException("Une erreur s'est produite");
      }
    }
    @GetMapping(value = "/all", produces = MediaType.APPLICATION_JSON_VALUE)
    public Flux<InvoiceProductDTO> findAllInvoiceProducts(
        @org.springdoc.core.annotations.ParameterObject Pageable pageable,
        ServerHttpRequest request
    ) {
       try{ log.debug("REST request to get a page of InvoiceProducts");
        return invoiceProductService
            .findAll(pageable);
    } catch (Exception e) {
        log.error("Une erreur s'est produite: {}", e.getMessage());
        throw new BusinessBadRequestException("Une erreur s'est produite");
    }
    }

    /**
     * {@code GET  /invoice-products/:id} : get the "id" invoiceProduct.
     *
     * @param id the id of the invoiceProductDTO to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the invoiceProductDTO, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/{id}")
    public Mono<ResponseEntity<InvoiceProductDTO>> getInvoiceProduct(@PathVariable Long id) {
      try{  log.debug("REST request to get InvoiceProduct : {}", id);
        Mono<InvoiceProductDTO> invoiceProductDTO = invoiceProductService.findOne(id);
        return ResponseUtil.wrapOrNotFound(invoiceProductDTO);
    } catch (Exception e) {
        log.error("Une erreur s'est produite : {}", e.getMessage());
        throw new BusinessBadRequestException("Une erreur s'est produite");
    }
    }

    /**
     * {@code DELETE  /invoice-products/:id} : delete the "id" invoiceProduct.
     *
     * @param id the id of the invoiceProductDTO to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/{id}")
    public Mono<ResponseEntity<Void>> deleteInvoiceProduct(@PathVariable Long id) {
        log.debug("REST request to delete InvoiceProduct : {}", id);
        try{return invoiceProductService
            .delete(id)
            .then(
                Mono.just(
                    ResponseEntity
                        .noContent()
                        .headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id.toString()))
                        .build()
                )
            );
    } catch (Exception e) {
        log.error(" Une erreur s'est produite: {}", e.getMessage());
        throw new BusinessBadRequestException("Une erreur s'est produite");
    }
    }
}
