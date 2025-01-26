package com.profac.app.service.mapper;

import org.junit.jupiter.api.BeforeEach;

class ProductMapperTest {

    private ProductMapper productMapper;

    @BeforeEach
    public void setUp() {
        productMapper = new ProductMapperImpl();
    }
}
