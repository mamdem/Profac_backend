package com.profac.app.service.mapper;

import org.junit.jupiter.api.BeforeEach;

class StockMapperTest {

    private StockMapper stockMapper;

    @BeforeEach
    public void setUp() {
        stockMapper = new StockMapperImpl();
    }
}
