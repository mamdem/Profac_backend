package com.profac.app.service.impl;

import com.profac.app.domain.Authority;
import com.profac.app.domain.Company;
import com.profac.app.domain.enumeration.CompanyStatus;
import com.profac.app.repository.AuthorityRepository;
import com.profac.app.repository.CompanyRepository;
import com.profac.app.security.SecurityUtils;
import com.profac.app.service.AppUserService;
import com.profac.app.service.CompanyService;
import com.profac.app.service.dto.AdminUserDTO;
import com.profac.app.service.dto.CompanyDTO;
import com.profac.app.service.mapper.CompanyMapper;
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

import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Service Implementation for managing {@link com.profac.app.domain.Company}.
 */
@Service
@Transactional
public class CompanyServiceImpl implements CompanyService {

    private final Logger log = LoggerFactory.getLogger(CompanyServiceImpl.class);

    private final CompanyRepository companyRepository;
    private final AuthorityRepository authorityRepository;
    private final UserResource userResource;
    private final AppUserService appUserService;
    public CompanyServiceImpl(CompanyRepository companyRepository,
                              AuthorityRepository authorityRepository,
                              UserResource userResource, AppUserService appUserService) {
        this.companyRepository = companyRepository;
        this.authorityRepository = authorityRepository;
        this.userResource = userResource;
        this.appUserService = appUserService;
    }

    @Override
    @Transactional
    public Mono<CompanyDTO> save(CompanyDTO companyDTO) {
        log.debug("Request to save Company : {}", companyDTO);
        List<String> authorityNames = Arrays.asList("ROLE_ADMIN", "ROLE_SELLER", "ROLE_CASHIER");
        Mono<Set<String>> authoritiesMono = Flux.fromIterable(authorityNames)
            .flatMap(authorityRepository::findById)
            .map(Authority::getName)
            .collect(Collectors.toSet());
        Company company = this.toEntity(companyDTO);
        company.setPassword(RandomUtil.generatePassword());
        company.setStatus(CompanyStatus.ACTIVE);
        AdminUserDTO adminUserDTO = new AdminUserDTO();
        adminUserDTO.setLogin(companyDTO.getPhoneNumber());
        adminUserDTO.setPassword(company.getPassword());

        return company.initAuditFields().then( authoritiesMono
                .flatMap(authorities -> {
                    adminUserDTO.setAuthorities(authorities);
                    return userResource.createUser(adminUserDTO);
                })
                .flatMap(user -> companyRepository.save(company)
                    .map(this::toDto)))
            .doOnSuccess(dto -> log.debug("Saved company: {}", dto));
    }

    @Override
    public Mono<CompanyDTO> update(CompanyDTO companyDTO) {
        log.debug("Request to update Company : {}", companyDTO);
        return companyRepository.save(this.toEntity(companyDTO)).map(this::toDto);
    }
    @Override
    public Mono<Company> findByPhoneNumber() {
        try{   return SecurityUtils.getCurrentUserLogin()
            .flatMap(login -> companyRepository.findByPhoneNumber(login)
                .switchIfEmpty(appUserService.findByPhoneNumber(login)
                    .flatMap(appUser -> companyRepository
                        .findById(appUser.getCompany().getId()))))
            .switchIfEmpty(Mono.error(new BusinessNotFoundException("No company find!")));
        } catch (Exception e) {
            log.error("Une erreur s'est produite: {}", e.getMessage());
            throw new BusinessBadRequestException("Une erreur s'est produite");
        }
    }

    @Override
    public Mono<CompanyDTO> partialUpdate(CompanyDTO companyDTO) {
        log.debug("Request to partially update Company : {}", companyDTO);

        return companyRepository
            .findById(companyDTO.getId())
            .map(existingCompany -> {
                this.partialUpdate(existingCompany, companyDTO);

                return existingCompany;
            })
            .flatMap(companyRepository::save)
            .map(this::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public Flux<CompanyDTO> findAll(Pageable pageable) {
        log.debug("Request to get all Companies");
        return companyRepository.findAllBy(pageable).map(this::toDto);
    }

    public Mono<Long> countAll() {
        return companyRepository.count();
    }

    @Override
    @Transactional(readOnly = true)
    public Mono<CompanyDTO> findOne(Long id) {
        log.debug("Request to get Company : {}", id);
        return companyRepository.findById(id).map(this::toDto);
    }

    @Override
    public Mono<Void> delete(Long id) {
        log.debug("Request to delete Company : {}", id);
        return companyRepository.deleteById(id);
    }
    public CompanyDTO toDto(Company entity) {
        if ( entity == null ) {
            return null;
        }

        CompanyDTO companyDTO = new CompanyDTO();

        companyDTO.setId( entity.getId() );
        companyDTO.setName( entity.getName() );
        companyDTO.setValidUntil( entity.getValidUntil() );
        companyDTO.setStatus( entity.getStatus() );
        companyDTO.setPassword( entity.getPassword() );
        companyDTO.setPhoneNumber(entity.getPhoneNumber());

        return companyDTO;
    }
    public Company toEntity(CompanyDTO dto) {
        if ( dto == null ) {
            return null;
        }
        Company company = new Company();

        company.setId( dto.getId() );
        company.setName( dto.getName() );
        company.setValidUntil( dto.getValidUntil() );
        company.setStatus( dto.getStatus() );
        company.setPassword( dto.getPassword() );
        company.setPhoneNumber(dto.getPhoneNumber());
        return company;
    }
    public void partialUpdate(Company entity, CompanyDTO dto) {
        if ( dto == null ) {
            return;
        }

        if ( dto.getId() != null ) {
            entity.setId( dto.getId() );
        }
        if ( dto.getName() != null ) {
            entity.setName( dto.getName() );
        }
        if ( dto.getValidUntil() != null ) {
            entity.setValidUntil( dto.getValidUntil() );
        }
        if ( dto.getStatus() != null ) {
            entity.setStatus( dto.getStatus() );
        }
        if ( dto.getPassword() != null ) {
            entity.setPassword( dto.getPassword() );
        }
        if ( dto.getPhoneNumber() != null ) {
            entity.setPhoneNumber( dto.getPhoneNumber() );
        }
    }
}
