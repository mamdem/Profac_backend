package com.profac.app.service.impl;

import com.profac.app.domain.Stock;
import com.profac.app.domain.enumeration.StockStatus;
import com.profac.app.repository.StockRepository;
import com.profac.app.service.ProductService;
import com.profac.app.service.StockService;
import com.profac.app.service.dto.ProductDTO;
import com.profac.app.service.dto.StockDTO;
import com.profac.app.service.mapper.StockMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.math.BigDecimal;

/**
 * Service Implementation for managing {@link com.profac.app.domain.Stock}.
 */
@Service
@Transactional
public class StockServiceImpl implements StockService {

    private final Logger log = LoggerFactory.getLogger(StockServiceImpl.class);

    private final StockRepository stockRepository;

    private final StockMapper stockMapper;
    private final ProductService productService;
    public StockServiceImpl(StockRepository stockRepository, StockMapper stockMapper, ProductService productService) {
        this.stockRepository = stockRepository;
        this.stockMapper = stockMapper;
        this.productService = productService;
    }

    @Override
    @Transactional
    public Mono<StockDTO> save(StockDTO stockDTO) {
        log.debug("Request to save Stock : {}", stockDTO);

        ProductDTO product = stockDTO.getProduct();
        stockDTO.setRemainingQuantity(stockDTO.getInitialQuantity());
        stockDTO.setStatus(StockStatus.ACTIVE);
        stockDTO.setTotalAmount(product.getAmount().multiply(new BigDecimal(stockDTO.getInitialQuantity())));
        stockDTO.setTotalAmountSold(BigDecimal.ZERO);
        Stock stock = stockMapper.toEntity(stockDTO);

        return stock.initAuditFields().then(productService.save(product)
            .flatMap(productDTO -> {
                stock.setProductId(productDTO.getId());
                return stockRepository.save(stock)
                    .map(stockMapper::toDto);
            }));
    }


    @Override
    public Mono<StockDTO> update(StockDTO stockDTO) {
        log.debug("Request to update Stock : {}", stockDTO);
        return stockRepository.save(stockMapper.toEntity(stockDTO)).map(stockMapper::toDto);
    }

    @Override
    public Mono<StockDTO> partialUpdate(StockDTO stockDTO) {
        log.debug("Request to partially update Stock : {}", stockDTO);

        return stockRepository
            .findById(stockDTO.getId())
            .map(existingStock -> {
                stockMapper.partialUpdate(existingStock, stockDTO);

                return existingStock;
            })
            .flatMap(stockRepository::save)
            .map(stockMapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public Flux<StockDTO> findAll(Pageable pageable) {
        log.debug("Request to get all Stocks");
        return stockRepository.findAllBy(pageable).map(stockMapper::toDto);
    }

    public Mono<Long> countAll() {
        return stockRepository.count();
    }

    @Override
    @Transactional(readOnly = true)
    public Mono<StockDTO> findOne(Long id) {
        log.debug("Request to get Stock : {}", id);
        return stockRepository.findById(id).map(stockMapper::toDto);
    }

    @Override
    public Mono<Void> delete(Long id) {
        log.debug("Request to delete Stock : {}", id);
        return stockRepository.deleteById(id);
    }
}
