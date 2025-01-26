package com.profac.app.service.impl;

import com.profac.app.domain.Category;
import com.profac.app.domain.Product;
import com.profac.app.domain.Stock;
import com.profac.app.domain.enumeration.CategoryStatus;
import com.profac.app.domain.enumeration.ProductStatus;
import com.profac.app.repository.AppUserRepository;
import com.profac.app.repository.ProductRepository;
import com.profac.app.repository.StockRepository;
import com.profac.app.security.SecurityUtils;
import com.profac.app.service.CategoryService;
import com.profac.app.service.CompanyService;
import com.profac.app.service.ProductService;
import com.profac.app.service.dto.CategoryDTO;
import com.profac.app.service.dto.ProductDTO;
import com.profac.app.service.mapper.CategoryMapper;
import com.profac.app.service.mapper.ProductMapper;
import com.profac.app.utils.exception.BusinessBadRequestException;
import com.profac.app.utils.exception.BusinessNotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Service Implementation for managing {@link com.profac.app.domain.Product}.
 */
@Service
@Transactional
public class ProductServiceImpl implements ProductService {

    private final Logger log = LoggerFactory.getLogger(ProductServiceImpl.class);

    private final ProductRepository productRepository;
    private final StockRepository stockRepository;

    private final ProductMapper productMapper;;
    private final CompanyService companyService;
    private final CategoryService categoryService;;
    private final CategoryMapper categoryMapper;
    @Value("${initial-number.product}") Integer initialProductNumber;

    public ProductServiceImpl(ProductRepository productRepository, StockRepository stockRepository, ProductMapper productMapper,
                              CompanyService companyService, CategoryService categoryService, CategoryMapper categoryMapper) {
        this.productRepository = productRepository;
        this.stockRepository = stockRepository;
        this.productMapper = productMapper;
        this.companyService = companyService;
        this.categoryService = categoryService;
        this.categoryMapper = categoryMapper;
    }

    @Override
    @Transactional
    public Mono<ProductDTO> save(ProductDTO productDTO) {
        log.debug("Request to save Product : {}", productDTO);

        CategoryDTO category = productDTO.getCategory();
        if (category == null) throw new BusinessBadRequestException("Category cannot be null");

        String name = category.getName();
        Mono<CategoryDTO> categoryMono = categoryService.findByName(name)
            .switchIfEmpty(Mono.defer(() -> {
                category.setName(name);
                category.setStatus(CategoryStatus.ACTIVE);
                Category entity = categoryMapper.toEntity(category);
                return entity.initAuditFields().then(categoryService.save(entity));
            }));

        return categoryMono.flatMap(existingCategory -> {
            productDTO.setCategory(existingCategory);
            productDTO.setStatus(ProductStatus.ACTIVE);

            Product product = productMapper.toEntity(productDTO);

            return product.initAuditFields().then(Mono.defer(() ->
                productRepository.count()
                    .map(count -> {
                        product.setProductNumber(initialProductNumber + count.intValue());
                        return product;
                    })
                    .flatMap(productRepository::save)
                    .map(productMapper::toDto)
                    .doOnSuccess(dto -> log.debug("Saved Product: {}", dto))
                    .doOnError(error -> log.error("Error saving product", error))
            ));
        });
    }

    @Override
    public Mono<ProductDTO> update(ProductDTO productDTO) {
        log.debug("Request to update Product : {}", productDTO);
        return productRepository.save(productMapper.toEntity(productDTO)).map(productMapper::toDto);
    }

    @Override
    public Mono<ProductDTO> partialUpdate(ProductDTO productDTO) {
        log.debug("Request to partially update Product : {}", productDTO);

        return productRepository
            .findById(productDTO.getId())
            .map(existingProduct -> {
                productMapper.partialUpdate(existingProduct, productDTO);

                return existingProduct;
            })
            .flatMap(productRepository::save)
            .map(productMapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public Flux<ProductDTO> findAll(Pageable pageable) {
        log.debug("Request to get all Products");
        return productRepository.findAllBy(pageable).map(productMapper::toDto);
    }

    public Mono<Long> countAll() {
        return productRepository.count();
    }

    @Override
    @Transactional(readOnly = true)
    public Mono<ProductDTO> findOne(Long id) {
        log.debug("Request to get Product : {}", id);
        return productRepository.findById(id).map(productMapper::toDto);
    }

    @Override
    public Mono<Void> delete(Long id) {
        log.debug("Request to delete Product : {}", id);
        return productRepository.deleteById(id);
    }

    public Flux<CategoryDTO> findAllCategoryByCompany() {
        log.debug("Request to get all categories by company");

        return SecurityUtils.getCurrentUserLogin()
            .flatMapMany(login ->
                companyService.findByPhoneNumber()
                    .flatMapMany(company ->
                        stockRepository.findAllByCompanyId(company.getId())
                            .map(Stock::getProduct)
                            .flatMap(product -> productRepository.findById(product.getId()))
                            .distinct()
                            .flatMap(product -> categoryService.findOne(product.getCategory().getId()))
                    )
            );
    }


    @Override
    public Mono<Product> findByProductNumber(Integer number) {
        log.debug("Request to get Product by number: {}", number);
        return productRepository.findByProductNumber(number)
            .switchIfEmpty(Mono.error(new BusinessNotFoundException("Product not found!")));
    }

}
