package com.profac.app.service.mapper;

import com.profac.app.domain.Invoice;
import com.profac.app.domain.Product;
import com.profac.app.service.dto.InvoiceDTO;
import com.profac.app.service.dto.ProductDTO;
import java.util.Set;
import java.util.stream.Collectors;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link Invoice} and its DTO {@link InvoiceDTO}.
 */
@Mapper(componentModel = "spring")
public interface InvoiceMapper extends EntityMapper<InvoiceDTO, Invoice> {
    @Mapping(target = "products", source = "products", qualifiedByName = "productIdSet")
    InvoiceDTO toDto(Invoice s);

    @Mapping(target = "removeProducts", ignore = true)
    Invoice toEntity(InvoiceDTO invoiceDTO);

    @Named("productId")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    ProductDTO toDtoProductId(Product product);

    @Named("productIdSet")
    default Set<ProductDTO> toDtoProductIdSet(Set<Product> product) {
        return product.stream().map(this::toDtoProductId).collect(Collectors.toSet());
    }
}
