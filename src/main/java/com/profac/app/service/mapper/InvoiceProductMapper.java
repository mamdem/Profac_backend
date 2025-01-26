package com.profac.app.service.mapper;

import com.profac.app.domain.Invoice;
import com.profac.app.domain.InvoiceProduct;
import com.profac.app.domain.Product;
import com.profac.app.service.dto.InvoiceDTO;
import com.profac.app.service.dto.InvoiceProductDTO;
import com.profac.app.service.dto.ProductDTO;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link InvoiceProduct} and its DTO {@link InvoiceProductDTO}.
 */
@Mapper(componentModel = "spring")
public interface InvoiceProductMapper extends EntityMapper<InvoiceProductDTO, InvoiceProduct> {
    @Mapping(target = "invoice", source = "invoice", qualifiedByName = "invoiceId")
    @Mapping(target = "product", source = "product", qualifiedByName = "productId")
    InvoiceProductDTO toDto(InvoiceProduct s);

    @Named("invoiceId")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    @Mapping(target = "invoiceNumber", source = "invoiceNumber")
    @Mapping(target = "customer", source = "customer")
    @Mapping(target = "invoiceDate", source = "invoiceDate")
    @Mapping(target = "status", source = "status")
    InvoiceDTO toDtoInvoiceId(Invoice invoice);

    @Named("productId")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    @Mapping(target = "productNumber", source = "productNumber")
    @Mapping(target = "name", source = "name")
    @Mapping(target = "amount", source = "amount")
    @Mapping(target = "description", source = "description")
    @Mapping(target = "status", source = "status")
    ProductDTO toDtoProductId(Product product);
}
