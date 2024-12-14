package com.profac.app.service.impl;

import com.profac.app.domain.Product;
import com.profac.app.domain.enumeration.CategoryStatus;
import com.profac.app.domain.enumeration.ProductStatus;
import com.profac.app.repository.AppUserRepository;
import com.profac.app.repository.CompanyRepository;
import com.profac.app.repository.ProductRepository;
import com.profac.app.security.SecurityUtils;
import com.profac.app.service.CategoryService;
import com.profac.app.service.ProductService;
import com.profac.app.service.dto.CategoryDTO;
import com.profac.app.service.dto.ProductDTO;
import com.profac.app.service.mapper.CategoryMapper;
import com.profac.app.service.mapper.ProductMapper;
import com.profac.app.utils.exception.BusinessBadRequestException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Pageable;
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

    private final ProductMapper productMapper;;
    private final CategoryMapper categoryMapper;;
    private final CompanyRepository companyRepository;;
    private final AppUserRepository appUserRepository;;
    private final CategoryService categoryService;;
    @Value("${initial-number.product}") Integer initialProductNumber;

    public ProductServiceImpl(ProductRepository productRepository, ProductMapper productMapper,
                              CategoryMapper categoryMapper, CompanyRepository companyRepository,
                              AppUserRepository appUserRepository, CategoryService categoryService) {
        this.productRepository = productRepository;
        this.productMapper = productMapper;
        this.categoryMapper = categoryMapper;
        this.companyRepository = companyRepository;
        this.appUserRepository = appUserRepository;
        this.categoryService = categoryService;
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
                return category.initAuditFields().then(categoryService.save(category));
            }));
        return categoryMono.flatMap(existingCategory -> {
            productDTO.setCategory(existingCategory);
            productDTO.setStatus(ProductStatus.ACTIVE);
            Product product = productMapper.toEntity(productDTO);
            return product.initAuditFields().then(Mono.defer(() -> productRepository.count()
                .map(count -> {
                    product.setProductNumber(initialProductNumber + count.intValue());
                    return product;
                })
                .flatMap(updatedProduct -> SecurityUtils.getCurrentUserLogin()
                    .flatMap(login -> companyRepository.findByName(login)
                        .flatMap(company -> {
                            updatedProduct.setCompany(company);
                            return productRepository.save(updatedProduct)
                                .map(productMapper::toDto);
                        })
                    )
                )))
                .doOnSuccess(dto -> log.debug("Saved Product: {}", dto))
                .doOnError(error -> log.error("Error saving product", error));
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
    @Override
    @Transactional(readOnly = true)
    public Flux<ProductDTO> findAllByCompany(Pageable pageable) {
        log.debug("Request to get all Products by company");
        return SecurityUtils.getCurrentUserLogin()
            .flatMapMany(login ->
                companyRepository.findByName(login)
                    .switchIfEmpty(appUserRepository.findByPhoneNumber(login).flatMap(appUser -> companyRepository.findById(appUser.getCompanyId())))
                    .flatMapMany(company ->  productRepository.findAllByCompanyId(company.getId(), pageable))
                    .map(productMapper::toDto)
            );
    }
    public Flux<CategoryDTO> findAllCategoryByCompany() {
        log.debug("Request to get all categories by company");
        return SecurityUtils.getCurrentUserLogin()
            .flatMapMany(login ->
                companyRepository.findByName(login)
                    .switchIfEmpty(appUserRepository.findByPhoneNumber(login).flatMap(appUser -> companyRepository.findById(appUser.getCompanyId())))
                    .flatMapMany(company ->  productRepository.findAllByCompanyId(company.getId()))
                    .map(Product::getCategoryId)
                    .distinct()
                    .flatMap(categoryService::findOne)
            );
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
}
