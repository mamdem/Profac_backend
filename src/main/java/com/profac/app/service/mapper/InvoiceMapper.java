package com.profac.app.service.mapper;

import com.profac.app.domain.Company;
import com.profac.app.domain.Invoice;
import com.profac.app.service.dto.CompanyDTO;
import com.profac.app.service.dto.InvoiceDTO;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link Invoice} and its DTO {@link InvoiceDTO}.
 */
@Mapper(componentModel = "spring")
public interface InvoiceMapper extends EntityMapper<InvoiceDTO, Invoice> {
    @Mapping(target = "company", source = "company", qualifiedByName = "companyId")
    InvoiceDTO toDto(Invoice s);

    @Named("companyId")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    @Mapping(target = "name", source = "name")
    @Mapping(target = "validUntil", source = "validUntil")
    @Mapping(target = "status", source = "status")
    CompanyDTO toDtoCompanyId(Company company);
}
