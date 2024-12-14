package com.profac.app.service.mapper;

import com.profac.app.domain.AppUser;
import com.profac.app.domain.Company;
import com.profac.app.domain.Image;
import com.profac.app.service.dto.AppUserDTO;
import com.profac.app.service.dto.CompanyDTO;
import com.profac.app.service.dto.ImageDTO;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link AppUser} and its DTO {@link AppUserDTO}.
 */
@Mapper(componentModel = "spring")
public interface AppUserMapper extends EntityMapper<AppUserDTO, AppUser> {
    @Mapping(target = "avatar", source = "avatar", qualifiedByName = "imageId")
    @Mapping(target = "company", source = "company", qualifiedByName = "companyId")
    AppUserDTO toDto(AppUser s);

    @Named("imageId")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    @Mapping(target = "url", source = "url")
    @Mapping(target = "status", source = "status")
    ImageDTO toDtoImageId(Image image);

    @Named("companyId")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    @Mapping(target = "createdBy", source = "createdBy")
    @Mapping(target = "createdDate", source = "createdDate")
    @Mapping(target = "lastModifiedBy", source = "lastModifiedBy")
    @Mapping(target = "lastModifiedDate", source = "lastModifiedDate")
    @Mapping(target = "name", source = "name")
    @Mapping(target = "validUntil", source = "validUntil")
    @Mapping(target = "status", source = "status")
    CompanyDTO toDtoCompanyId(Company company);
}
