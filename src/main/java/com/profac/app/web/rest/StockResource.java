package com.profac.app.web.rest;

import com.profac.app.repository.StockRepository;
import com.profac.app.security.AuthoritiesConstants;
import com.profac.app.service.StockService;
import com.profac.app.service.dto.StockDTO;
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
import org.springframework.http.codec.multipart.FilePart;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.util.UriComponentsBuilder;
import reactor.core.publisher.Mono;
import tech.jhipster.web.util.HeaderUtil;
import tech.jhipster.web.util.PaginationUtil;
import tech.jhipster.web.util.reactive.ResponseUtil;

/**
 * REST controller for managing {@link com.profac.app.domain.Stock}.
 */
@RestController
@RequestMapping("/api/stocks")
public class StockResource {

    private final Logger log = LoggerFactory.getLogger(StockResource.class);

    private static final String ENTITY_NAME = "stock";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final StockService stockService;

    private final StockRepository stockRepository;

    public StockResource(StockService stockService, StockRepository stockRepository) {
        this.stockService = stockService;
        this.stockRepository = stockRepository;
    }

    /**
     * {@code POST  /stocks} : Create a new stock.
     *
     * @param stockDTO the stockDTO to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new stockDTO, or with status {@code 400 (Bad Request)} if the stock has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("")
    @PreAuthorize("hasAuthority(\"" + AuthoritiesConstants.ADMIN + "\")")
    public Mono<ResponseEntity<StockDTO>> createStock(@Valid @ModelAttribute StockDTO stockDTO, @RequestPart("file") FilePart image) throws URISyntaxException {
       try{
        log.debug("REST request to save Stock : {}", stockDTO);
        if (stockDTO.getId() != null) {
            throw new BadRequestAlertException("A new stock cannot already have an ID", ENTITY_NAME, "idexists");
        }
        return stockService
            .save(stockDTO, image)
            .map(result -> {
                try {
                    return ResponseEntity
                        .created(new URI("/api/stocks/" + result.getId()))
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
     * {@code PUT  /stocks/:id} : Updates an existing stock.
     *
     * @param id the id of the stockDTO to save.
     * @param stockDTO the stockDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated stockDTO,
     * or with status {@code 400 (Bad Request)} if the stockDTO is not valid,
     * or with status {@code 500 (Internal Server Error)} if the stockDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority(\"" + AuthoritiesConstants.ADMIN + "\")")
    public Mono<ResponseEntity<StockDTO>> updateStock(
        @PathVariable(value = "id", required = false) final Long id,
        @Valid @RequestBody StockDTO stockDTO
    ) throws URISyntaxException {
     try {
         log.debug("REST request to update Stock : {}, {}", id, stockDTO);
        if (stockDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, stockDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        return stockRepository
            .existsById(id)
            .flatMap(exists -> {
                if (!exists) {
                    return Mono.error(new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound"));
                }

                return stockService
                    .update(stockDTO)
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
     * {@code PATCH  /stocks/:id} : Partial updates given fields of an existing stock, field will ignore if it is null
     *
     * @param id the id of the stockDTO to save.
     * @param stockDTO the stockDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated stockDTO,
     * or with status {@code 400 (Bad Request)} if the stockDTO is not valid,
     * or with status {@code 404 (Not Found)} if the stockDTO is not found,
     * or with status {@code 500 (Internal Server Error)} if the stockDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/{id}", consumes = { "application/json", "application/merge-patch+json" })
    @PreAuthorize("hasAuthority(\"" + AuthoritiesConstants.ADMIN + "\")")
    public Mono<ResponseEntity<StockDTO>> partialUpdateStock(
        @PathVariable(value = "id", required = false) final Long id,
        @NotNull @RequestBody StockDTO stockDTO
    ) throws URISyntaxException {
     try {   log.debug("REST request to partial update Stock partially : {}, {}", id, stockDTO);
        if (stockDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, stockDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        return stockRepository
            .existsById(id)
            .flatMap(exists -> {
                if (!exists) {
                    return Mono.error(new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound"));
                }

                Mono<StockDTO> result = stockService.partialUpdate(stockDTO);

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
     * {@code GET  /stocks} : get all the stocks.
     *
     * @param pageable the pagination information.
     * @param request a {@link ServerHttpRequest} request.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of stocks in body.
     */
    @GetMapping(value = "", produces = MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorize("hasAuthority(\"" + AuthoritiesConstants.ADMIN + "\")")
    public Mono<ResponseEntity<List<StockDTO>>> getAllStocks(
        @org.springdoc.core.annotations.ParameterObject Pageable pageable,
        ServerHttpRequest request,
        @RequestParam(defaultValue = "0") int page,
        @RequestParam(defaultValue = "10") int size
    ) {
      try {  log.debug("REST request to get a page of Stocks");
        return stockService
            .countAll()
            .zipWith(stockService.findAll(page, size).collectList())
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

    /**
     * {@code GET  /stocks/:id} : get the "id" stock.
     *
     * @param id the id of the stockDTO to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the stockDTO, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/{id}")
    public Mono<ResponseEntity<StockDTO>> getStock(@PathVariable Long id) {
       try{ log.debug("REST request to get Stock : {}", id);
        Mono<StockDTO> stockDTO = stockService.findOne(id);
        return ResponseUtil.wrapOrNotFound(stockDTO);
    } catch (Exception e) {
        log.error("Une erreur s'est produite: {}", e.getMessage());
        throw new BusinessBadRequestException("Une erreur s'est produite");
    }
    }

    /**
     * {@code DELETE  /stocks/:id} : delete the "id" stock.
     *
     * @param id the id of the stockDTO to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority(\"" + AuthoritiesConstants.ADMIN + "\")")
    public Mono<ResponseEntity<Void>> deleteStock(@PathVariable Long id) {
     try{   log.debug("REST request to delete Stock : {}", id);
        return stockService
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
}
