package com.profac.app.service.impl;

import com.profac.app.domain.AppUser;
import com.profac.app.domain.Authority;
import com.profac.app.domain.enumeration.AppUserStatus;
import com.profac.app.domain.enumeration.UserType;
import com.profac.app.repository.AppUserRepository;
import com.profac.app.repository.AuthorityRepository;
import com.profac.app.repository.CompanyRepository;
import com.profac.app.security.SecurityUtils;
import com.profac.app.service.AppUserService;
import com.profac.app.service.dto.AdminUserDTO;
import com.profac.app.service.dto.AppUserDTO;
import com.profac.app.service.mapper.AppUserMapper;
import com.profac.app.utils.exception.BusinessBadRequestException;
import com.profac.app.utils.exception.BusinessNotFoundException;
import com.profac.app.web.rest.UserResource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import tech.jhipster.security.RandomUtil;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Service Implementation for managing {@link com.profac.app.domain.AppUser}.
 */
@Service
@Transactional
public class AppUserServiceImpl implements AppUserService {

    private final Logger log = LoggerFactory.getLogger(AppUserServiceImpl.class);

    private final AppUserRepository appUserRepository;

    private final AppUserMapper appUserMapper;
    private final AuthorityRepository authorityRepository;
    private final UserResource userResource;
    private final CompanyRepository companyRepository;
    public AppUserServiceImpl(AppUserRepository appUserRepository, AppUserMapper appUserMapper, AuthorityRepository authorityRepository, UserResource userResource, CompanyRepository companyRepository) {
        this.appUserRepository = appUserRepository;
        this.appUserMapper = appUserMapper;
        this.authorityRepository = authorityRepository;
        this.userResource = userResource;
        this.companyRepository = companyRepository;
    }

    @Override
    public Mono<AppUserDTO> save(AppUserDTO appUserDTO) {
        log.debug("Request to save AppUser : {}", appUserDTO);
        UserType userType = appUserDTO.getUserType();
        Set<UserType> validTypes = Set.of(UserType.CASHIER, UserType.SELLER);
        if (!validTypes.contains(userType)) {
            log.error("User type not found");
            throw new BusinessBadRequestException("User type not found: {}", userType.name());
        }
        String role = userType.equals(UserType.CASHIER) ? "ROLE_CASHIER" : "ROLE_SELLER";
        List<String> authorityNames = List.of(role);

        AppUser appUser = appUserMapper.toEntity(appUserDTO);
        appUser.setPassword(RandomUtil.generatePassword());
        appUser.setStatus(AppUserStatus.ACTIVE);
        Mono<Set<String>> authoritiesMono = Flux.fromIterable(authorityNames)
            .flatMap(authorityRepository::findById)
            .map(Authority::getName)
            .collect(Collectors.toSet());
        AdminUserDTO adminUserDTO = new AdminUserDTO();
        adminUserDTO.setLogin(appUser.getPhoneNumber());
        adminUserDTO.setPassword(appUser.getPassword());
        return SecurityUtils.getCurrentUserLogin()
            .flatMap(userLogin -> {
                log.debug("Logged user: {}", userLogin);
                appUser.setCreatedBy(userLogin);
                appUser.setLastModifiedBy(userLogin);
                return companyRepository.findByPhoneNumber(userLogin)
                    .switchIfEmpty(Mono.error(new BusinessNotFoundException("Company not found")))
                    .flatMap(company -> authoritiesMono
                        .flatMap(authorities -> {
                            adminUserDTO.setAuthorities(authorities);
                            return userResource.createUser(adminUserDTO);
                        })
                        .flatMap(user -> {
                            appUser.setCompany(company);
                            return appUserRepository.save(appUser)
                                .map(appUserMapper::toDto);
                        }))
                    .doOnSuccess(dto -> log.debug("Saved user {}: {}",userType, dto));
            });

    }

    @Override
    public Mono<AppUserDTO> update(AppUserDTO appUserDTO) {
        log.debug("Request to update AppUser : {}", appUserDTO);
        return appUserRepository.save(appUserMapper.toEntity(appUserDTO)).map(appUserMapper::toDto);
    }

    @Override
    public Mono<AppUserDTO> partialUpdate(AppUserDTO appUserDTO) {
        log.debug("Request to partially update AppUser : {}", appUserDTO);

        return appUserRepository
            .findById(appUserDTO.getId())
            .map(existingAppUser -> {
                appUserMapper.partialUpdate(existingAppUser, appUserDTO);

                return existingAppUser;
            })
            .flatMap(appUserRepository::save)
            .map(appUserMapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public Flux<AppUserDTO> findAll(Pageable pageable) {
        log.debug("Request to get all AppUsers");
        return appUserRepository.findAllBy(pageable).map(appUserMapper::toDto);
    }

    public Mono<Long> countAll() {
        return appUserRepository.count();
    }

    @Override
    @Transactional(readOnly = true)
    public Mono<AppUserDTO> findOne(Long id) {
        log.debug("Request to get AppUser : {}", id);
        return appUserRepository.findById(id).map(appUserMapper::toDto);
    }
    @Override
    @Transactional(readOnly = true)
    public Mono<AppUserDTO> findByPhoneNumber(String phone) {
        log.debug("Request to get AppUser : {}", phone);
        return appUserRepository.findByPhoneNumber(phone).map(appUserMapper::toDto);
    }

    @Override
    public Mono<Void> delete(Long id) {
        log.debug("Request to delete AppUser : {}", id);
        return appUserRepository.deleteById(id);
    }
}
