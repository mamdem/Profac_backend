package com.profac.app.service.mapper;

import com.profac.app.domain.Image;
import com.profac.app.domain.Product;
import com.profac.app.service.dto.ImageDTO;
import com.profac.app.service.dto.ProductDTO;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link Image} and its DTO {@link ImageDTO}.
 */
@Mapper(componentModel = "spring")
public interface ImageMapper extends EntityMapper<ImageDTO, Image> {
    @Mapping(target = "product", source = "product", qualifiedByName = "productId")
    ImageDTO toDto(Image s);

    @Named("productId")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    ProductDTO toDtoProductId(Product product);
}
