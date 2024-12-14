package com.profac.app.repository;

import com.profac.app.domain.AppUser;
import org.springframework.data.domain.Pageable;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Spring Data R2DBC repository for the AppUser entity.
 */
@SuppressWarnings("unused")
@Repository
public interface AppUserRepository extends ReactiveCrudRepository<AppUser, Long>, AppUserRepositoryInternal {
    Flux<AppUser> findAllBy(Pageable pageable);

    @Query("SELECT * FROM app_user entity WHERE entity.avatar_id = :id")
    Flux<AppUser> findByAvatar(Long id);

    @Query("SELECT * FROM app_user entity WHERE entity.avatar_id IS NULL")
    Flux<AppUser> findAllWhereAvatarIsNull();

    @Query("SELECT * FROM app_user entity WHERE entity.company_id = :id")
    Flux<AppUser> findByCompany(Long id);

    @Query("SELECT * FROM app_user entity WHERE entity.company_id IS NULL")
    Flux<AppUser> findAllWhereCompanyIsNull();

    @Override
    <S extends AppUser> Mono<S> save(S entity);

    @Override
    Flux<AppUser> findAll();

    @Override
    Mono<AppUser> findById(Long id);
    Mono<AppUser> findByPhoneNumber(String phone);

    @Override
    Mono<Void> deleteById(Long id);
}

interface AppUserRepositoryInternal {
    <S extends AppUser> Mono<S> save(S entity);

    Flux<AppUser> findAllBy(Pageable pageable);

    Flux<AppUser> findAll();

    Mono<AppUser> findById(Long id);
    // this is not supported at the moment because of https://github.com/jhipster/generator-jhipster/issues/18269
    // Flux<AppUser> findAllBy(Pageable pageable, Criteria criteria);
}
