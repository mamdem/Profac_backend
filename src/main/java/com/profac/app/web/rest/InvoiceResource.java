package com.profac.app.web.rest;

import com.profac.app.repository.InvoiceRepository;
import com.profac.app.security.AuthoritiesConstants;
import com.profac.app.service.InvoiceService;
import com.profac.app.service.dto.InvoiceDTO;
import com.profac.app.service.dto.InvoiceResponseDTO;
import com.profac.app.utils.exception.BusinessBadRequestException;
import com.profac.app.web.rest.errors.BadRequestAlertException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Objects;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import reactor.core.publisher.Mono;
import tech.jhipster.web.util.HeaderUtil;
import tech.jhipster.web.util.reactive.ResponseUtil;

/**
 * REST controller for managing {@link com.profac.app.domain.Invoice}.
 */
@RestController
@RequestMapping("/api/invoices")
public class InvoiceResource {

    private final Logger log = LoggerFactory.getLogger(InvoiceResource.class);

    private static final String ENTITY_NAME = "invoice";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final InvoiceService invoiceService;

    private final InvoiceRepository invoiceRepository;

    public InvoiceResource(InvoiceService invoiceService, InvoiceRepository invoiceRepository) {
        this.invoiceService = invoiceService;
        this.invoiceRepository = invoiceRepository;
    }

    /**
     * {@code POST  /invoices} : Create a new invoice.
     *
     * @param invoiceDTO the invoiceDTO to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new invoiceDTO, or with status {@code 400 (Bad Request)} if the invoice has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("")
    @PreAuthorize("hasAnyAuthority('" + AuthoritiesConstants.ADMIN + "', '" + AuthoritiesConstants.CASHIER + "', '" + AuthoritiesConstants.SELLER + "')")
    public Mono<ResponseEntity<InvoiceDTO>> createInvoice(@RequestBody InvoiceDTO invoiceDTO) throws URISyntaxException {
        try {  log.debug("REST request to save Invoice : {}", invoiceDTO);
            if (invoiceDTO.getId() != null) {
                throw new BadRequestAlertException("A new invoice cannot already have an ID", ENTITY_NAME, "idexists");
            }
            return invoiceService
                .save(invoiceDTO)
                .map(result -> {
                    try {
                        return ResponseEntity
                            .created(new URI("/api/invoices/" + result.getId()))
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
     * {@code PUT  /invoices/:id} : Updates an existing invoice.
     *
     * @param id the id of the invoiceDTO to save.
     * @param invoiceDTO the invoiceDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated invoiceDTO,
     * or with status {@code 400 (Bad Request)} if the invoiceDTO is not valid,
     * or with status {@code 500 (Internal Server Error)} if the invoiceDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/{id}")
    @PreAuthorize("hasAnyAuthority('" + AuthoritiesConstants.ADMIN + "', '" + AuthoritiesConstants.CASHIER + "', '" + AuthoritiesConstants.SELLER + "')")
    public Mono<ResponseEntity<InvoiceDTO>> updateInvoice(
        @PathVariable(value = "id", required = false) final Long id,
        @RequestBody InvoiceDTO invoiceDTO
    ) throws URISyntaxException {
        try {   log.debug("REST request to update Invoice : {}, {}", id, invoiceDTO);
            if (invoiceDTO.getId() == null) {
                throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
            }
            if (!Objects.equals(id, invoiceDTO.getId())) {
                throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
            }

            return invoiceRepository
                .existsById(id)
                .flatMap(exists -> {
                    if (!exists) {
                        return Mono.error(new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound"));
                    }

                    return invoiceService
                        .update(invoiceDTO)
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
     * {@code PATCH  /invoices/:id} : Partial updates given fields of an existing invoice, field will ignore if it is null
     *
     * @param id the id of the invoiceDTO to save.
     * @param invoiceDTO the invoiceDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated invoiceDTO,
     * or with status {@code 400 (Bad Request)} if the invoiceDTO is not valid,
     * or with status {@code 404 (Not Found)} if the invoiceDTO is not found,
     * or with status {@code 500 (Internal Server Error)} if the invoiceDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/{id}", consumes = { "application/json", "application/merge-patch+json" })
    @PreAuthorize("hasAnyAuthority('" + AuthoritiesConstants.ADMIN + "', '" + AuthoritiesConstants.CASHIER + "', '" + AuthoritiesConstants.SELLER + "')")
    public Mono<ResponseEntity<InvoiceDTO>> partialUpdateInvoice(
        @PathVariable(value = "id", required = false) final Long id,
        @RequestBody InvoiceDTO invoiceDTO
    ) throws URISyntaxException {
        try{ log.debug("REST request to partial update Invoice partially : {}, {}", id, invoiceDTO);
            if (invoiceDTO.getId() == null) {
                throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
            }
            if (!Objects.equals(id, invoiceDTO.getId())) {
                throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
            }

            return invoiceRepository
                .existsById(id)
                .flatMap(exists -> {
                    if (!exists) {
                        return Mono.error(new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound"));
                    }

                    Mono<InvoiceDTO> result = invoiceService.partialUpdate(invoiceDTO);

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

    @GetMapping(value = "", produces = MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorize("hasAnyAuthority('" + AuthoritiesConstants.ADMIN + "', '" + AuthoritiesConstants.CASHIER + "', '" + AuthoritiesConstants.SELLER + "')")
    public Mono<ResponseEntity<Page<InvoiceResponseDTO>>> getAllInvoices(
        @RequestParam(defaultValue = "0") int page,
        @RequestParam(defaultValue = "10") int size
    ) {
        try{  log.debug("REST request to get a page of Invoices");
            return invoiceService.findAll(page, size)
                .map(ResponseEntity::ok);
        } catch (Exception e) {
            log.error("Une erreur s'est produite: {}", e.getMessage());
            throw new BusinessBadRequestException("Une erreur s'est produite");
        }
    }

    /**
     * {@code GET  /invoices/:id} : get the "id" invoice.
     *
     * @param id the id of the invoiceDTO to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the invoiceDTO, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/{id}")
    @PreAuthorize("hasAnyAuthority('" + AuthoritiesConstants.ADMIN + "', '" + AuthoritiesConstants.CASHIER + "', '" + AuthoritiesConstants.SELLER + "')")
    public Mono<ResponseEntity<InvoiceDTO>> getInvoice(@PathVariable Long id) {
        try{ log.debug("REST request to get Invoice : {}", id);
            Mono<InvoiceDTO> invoiceDTO = invoiceService.findOne(id);
            return ResponseUtil.wrapOrNotFound(invoiceDTO);
        } catch (Exception e) {
            log.error("Une erreur s'est produite: {}", e.getMessage());
            throw new BusinessBadRequestException("Une erreur s'est produite");
        }
    }

    /**
     * {@code DELETE  /invoices/:id} : delete the "id" invoice.
     *
     * @param id the id of the invoiceDTO to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyAuthority('" + AuthoritiesConstants.ADMIN + "', '" + AuthoritiesConstants.CASHIER + "', '" + AuthoritiesConstants.SELLER + "')")
    public Mono<ResponseEntity<Void>> deleteInvoice(@PathVariable Long id) {
        try{log.debug("REST request to delete Invoice : {}", id);
            return invoiceService
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
            log.error("Une erreur s'est produite: {}", e.getMessage());
            throw new BusinessBadRequestException("Une erreur s'est produite");
        }
    }
    @GetMapping(value = "/details", produces = MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorize("hasAnyAuthority('" + AuthoritiesConstants.ADMIN + "', '" + AuthoritiesConstants.CASHIER + "', '" + AuthoritiesConstants.SELLER + "')")
    public Mono<ResponseEntity<InvoiceResponseDTO>> findByInvoiceNumber(
        @RequestParam("invoiceNumber") Long invoiceNumber) {
        log.debug("REST request to get Invoices by number: {}", invoiceNumber);
        return invoiceService.findByInvoiceNumber(invoiceNumber)
            .map(ResponseEntity::ok)
            .defaultIfEmpty(ResponseEntity.notFound().build());
    }
}
