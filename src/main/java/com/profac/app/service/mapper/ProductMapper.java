package com.profac.app.service.mapper;

import com.profac.app.domain.Category;
import com.profac.app.domain.Company;
import com.profac.app.domain.Product;
import com.profac.app.service.dto.CategoryDTO;
import com.profac.app.service.dto.CompanyDTO;
import com.profac.app.service.dto.ProductDTO;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link Product} and its DTO {@link ProductDTO}.
 */
@Mapper(componentModel = "spring")
public interface ProductMapper extends EntityMapper<ProductDTO, Product> {
    @Mapping(target = "category", source = "category", qualifiedByName = "categoryId")
    @Mapping(target = "company", source = "company", qualifiedByName = "companyId")
    ProductDTO toDto(Product s);

    @Named("categoryId")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    @Mapping(target = "name", source = "name")
    @Mapping(target = "status", source = "status")
    CategoryDTO toDtoCategoryId(Category category);

    @Named("companyId")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    @Mapping(target = "name", source = "name")
    @Mapping(target = "validUntil", source = "validUntil")
    @Mapping(target = "status", source = "status")
    CompanyDTO toDtoCompanyId(Company company);
}
