package com.profac.app.service.impl;

import com.profac.app.domain.Authority;
import com.profac.app.domain.Company;
import com.profac.app.domain.enumeration.CompanyStatus;
import com.profac.app.repository.AuthorityRepository;
import com.profac.app.repository.CompanyRepository;
import com.profac.app.service.CompanyService;
import com.profac.app.service.dto.AdminUserDTO;
import com.profac.app.service.dto.CompanyDTO;
import com.profac.app.service.mapper.CompanyMapper;
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

    private final CompanyMapper companyMapper;
    private final AuthorityRepository authorityRepository;
    private final UserResource userResource;
    public CompanyServiceImpl(CompanyRepository companyRepository, CompanyMapper companyMapper, AuthorityRepository authorityRepository, UserResource userResource) {
        this.companyRepository = companyRepository;
        this.companyMapper = companyMapper;
        this.authorityRepository = authorityRepository;
        this.userResource = userResource;
    }

    @Override
    @Transactional
    public Mono<CompanyDTO> save(CompanyDTO companyDTO) {
        log.debug("Request to save Company : {}", companyDTO);

        List<String> authorityNames = Arrays.asList("ROLE_ADMIN", "ROLE_SELLER", "ROLE_CASHIER");

        Company company = companyMapper.toEntity(companyDTO);
        company.setPassword(RandomUtil.generatePassword());
        company.setStatus(CompanyStatus.ACTIVE);
        Mono<Set<String>> authoritiesMono = Flux.fromIterable(authorityNames)
            .flatMap(authorityRepository::findById)
            .map(Authority::getName)
            .collect(Collectors.toSet());

        AdminUserDTO adminUserDTO = new AdminUserDTO();
        adminUserDTO.setLogin(company.getName());
        adminUserDTO.setPassword(company.getPassword());
        return company.initAuditFields().then( authoritiesMono
            .flatMap(authorities -> {
                adminUserDTO.setAuthorities(authorities);
                return userResource.createUser(adminUserDTO);
            })
            .flatMap(user -> companyRepository.save(company)
                .map(companyMapper::toDto)))
            .doOnSuccess(dto -> log.debug("Saved company: {}", dto));
    }

    @Override
    public Mono<CompanyDTO> update(CompanyDTO companyDTO) {
        log.debug("Request to update Company : {}", companyDTO);
        return companyRepository.save(companyMapper.toEntity(companyDTO)).map(companyMapper::toDto);
    }

    @Override
    public Mono<CompanyDTO> partialUpdate(CompanyDTO companyDTO) {
        log.debug("Request to partially update Company : {}", companyDTO);

        return companyRepository
            .findById(companyDTO.getId())
            .map(existingCompany -> {
                companyMapper.partialUpdate(existingCompany, companyDTO);

                return existingCompany;
            })
            .flatMap(companyRepository::save)
            .map(companyMapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public Flux<CompanyDTO> findAll(Pageable pageable) {
        log.debug("Request to get all Companies");
        return companyRepository.findAllBy(pageable).map(companyMapper::toDto);
    }

    public Mono<Long> countAll() {
        return companyRepository.count();
    }

    @Override
    @Transactional(readOnly = true)
    public Mono<CompanyDTO> findOne(Long id) {
        log.debug("Request to get Company : {}", id);
        return companyRepository.findById(id).map(companyMapper::toDto);
    }

    @Override
    public Mono<Void> delete(Long id) {
        log.debug("Request to delete Company : {}", id);
        return companyRepository.deleteById(id);
    }
}
