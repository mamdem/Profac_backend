package com.profac.app.service.impl;

import com.profac.app.domain.enumeration.ImageStatus;
import com.profac.app.repository.ImageRepository;
import com.profac.app.service.ImageService;
import com.profac.app.service.dto.ImageDTO;
import com.profac.app.service.mapper.ImageMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Service Implementation for managing {@link com.profac.app.domain.Image}.
 */
@Service
@Transactional
public class ImageServiceImpl implements ImageService {

    private final Logger log = LoggerFactory.getLogger(ImageServiceImpl.class);

    private final ImageRepository imageRepository;

    private final ImageMapper imageMapper;

    public ImageServiceImpl(ImageRepository imageRepository, ImageMapper imageMapper) {
        this.imageRepository = imageRepository;
        this.imageMapper = imageMapper;
    }

    @Override
    public Mono<ImageDTO> save(ImageDTO imageDTO) {
        log.debug("Request to save Image : {}", imageDTO);
        imageDTO.setStatus(ImageStatus.ACTIVE);
        return imageRepository.save(imageMapper.toEntity(imageDTO)).map(imageMapper::toDto);
    }

    @Override
    public Mono<ImageDTO> update(ImageDTO imageDTO) {
        log.debug("Request to update Image : {}", imageDTO);
        return imageRepository.save(imageMapper.toEntity(imageDTO)).map(imageMapper::toDto);
    }

    @Override
    public Mono<ImageDTO> partialUpdate(ImageDTO imageDTO) {
        log.debug("Request to partially update Image : {}", imageDTO);

        return imageRepository
            .findById(imageDTO.getId())
            .map(existingImage -> {
                imageMapper.partialUpdate(existingImage, imageDTO);

                return existingImage;
            })
            .flatMap(imageRepository::save)
            .map(imageMapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public Flux<ImageDTO> findAll(Pageable pageable) {
        log.debug("Request to get all Images");
        return imageRepository.findAllBy(pageable).map(imageMapper::toDto);
    }

    /**
     *  Get all the images where AppUser is {@code null}.
     *  @return the list of entities.
     */
    @Transactional(readOnly = true)
    public Flux<ImageDTO> findAllWhereAppUserIsNull() {
        log.debug("Request to get all images where AppUser is null");
        return imageRepository.findAllWhereAppUserIsNull().map(imageMapper::toDto);
    }

    public Mono<Long> countAll() {
        return imageRepository.count();
    }

    @Override
    @Transactional(readOnly = true)
    public Mono<ImageDTO> findOne(Long id) {
        log.debug("Request to get Image : {}", id);
        return imageRepository.findById(id).map(imageMapper::toDto);
    }

    @Override
    public Mono<Void> delete(Long id) {
        log.debug("Request to delete Image : {}", id);
        return imageRepository.deleteById(id);
    }
}
