package com.profac.app.service.mapper;

import org.junit.jupiter.api.BeforeEach;

class AppUserMapperTest {

    private AppUserMapper appUserMapper;

    @BeforeEach
    public void setUp() {
        appUserMapper = new AppUserMapperImpl();
    }
}
