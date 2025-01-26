package com.profac.app.service.impl;

import com.profac.app.domain.Category;
import com.profac.app.repository.CategoryRepository;
import com.profac.app.service.CategoryService;
import com.profac.app.service.dto.CategoryDTO;
import com.profac.app.service.mapper.CategoryMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Service Implementation for managing {@link com.profac.app.domain.Category}.
 */
@Service
@Transactional
public class CategoryServiceImpl implements CategoryService {

    private final Logger log = LoggerFactory.getLogger(CategoryServiceImpl.class);

    private final CategoryRepository categoryRepository;

    private final CategoryMapper categoryMapper;

    public CategoryServiceImpl(CategoryRepository categoryRepository, CategoryMapper categoryMapper) {
        this.categoryRepository = categoryRepository;
        this.categoryMapper = categoryMapper;
    }

    @Override
    public Mono<CategoryDTO> save(Category categoryDTO) {
        log.debug("Request to save Category : {}", categoryDTO);
        return categoryRepository.save(categoryDTO).map(categoryMapper::toDto);
    }

    @Override
    public Mono<CategoryDTO> update(CategoryDTO categoryDTO) {
        log.debug("Request to update Category : {}", categoryDTO);
        return categoryRepository.save(categoryMapper.toEntity(categoryDTO)).map(categoryMapper::toDto);
    }

    @Override
    public Mono<CategoryDTO> partialUpdate(CategoryDTO categoryDTO) {
        log.debug("Request to partially update Category : {}", categoryDTO);

        return categoryRepository
            .findById(categoryDTO.getId())
            .map(existingCategory -> {
                categoryMapper.partialUpdate(existingCategory, categoryDTO);

                return existingCategory;
            })
            .flatMap(categoryRepository::save)
            .map(categoryMapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public Flux<CategoryDTO> findAll(Pageable pageable) {
        log.debug("Request to get all Categories");
        return categoryRepository.findAllBy(pageable).map(categoryMapper::toDto);
    }
    @Override
    @Transactional(readOnly = true)
    public Flux<CategoryDTO> findAllByCreatedBy(String name) {
        log.debug("Request to get all Categories");
        return categoryRepository.findAllByCreatedBy(name).map(categoryMapper::toDto);
    }

    public Mono<Long> countAll() {
        return categoryRepository.count();
    }

    @Override
    @Transactional(readOnly = true)
    public Mono<CategoryDTO> findOne(Long id) {
        log.debug("Request to get Category : {}", id);
        return categoryRepository.findById(id).map(categoryMapper::toDto);
    }

    @Override
    public Mono<CategoryDTO> findByName(String name) {
        log.debug("Request to get Category : {}", name);
        return categoryRepository.findByName(name).map(categoryMapper::toDto);
    }

    @Override
    public Mono<Void> delete(Long id) {
        log.debug("Request to delete Category : {}", id);
        return categoryRepository.deleteById(id);
    }
}
