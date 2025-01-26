package com.profac.app.service.mapper;

import com.profac.app.domain.Company;
import com.profac.app.domain.Product;
import com.profac.app.domain.Stock;
import com.profac.app.service.dto.CompanyDTO;
import com.profac.app.service.dto.ProductDTO;
import com.profac.app.service.dto.StockDTO;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link Stock} and its DTO {@link StockDTO}.
 */
@Mapper(componentModel = "spring")
public interface StockMapper extends EntityMapper<StockDTO, Stock> {
    @Mapping(target = "company", source = "company", qualifiedByName = "companyId")
    @Mapping(target = "product", source = "product", qualifiedByName = "productId")
    StockDTO toDto(Stock s);

    @Named("productId")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    @Mapping(target = "productNumber", source = "productNumber")
    @Mapping(target = "name", source = "name")
    @Mapping(target = "amount", source = "amount")
    @Mapping(target = "description", source = "description")
    @Mapping(target = "status", source = "status")
    ProductDTO toDtoProductId(Product product);

    @Named("companyId")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    @Mapping(target = "name", source = "name")
    @Mapping(target = "validUntil", source = "validUntil")
    @Mapping(target = "status", source = "status")
    CompanyDTO toDtoCompanyId(Company company);
}
