package com.profac.app.domain;

import java.util.Random;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicLong;

public class AppUserTestSamples {

    private static final Random random = new Random();
    private static final AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    public static AppUser getAppUserSample1() {
        return new AppUser()
            .id(1L)
            .firstName("firstName1")
            .lastName("lastName1")
            .password("password1")
            .phoneNumber("phoneNumber1")
            .address("address1");
    }

    public static AppUser getAppUserSample2() {
        return new AppUser()
            .id(2L)
            .firstName("firstName2")
            .lastName("lastName2")
            .password("password2")
            .phoneNumber("phoneNumber2")
            .address("address2");
    }

    public static AppUser getAppUserRandomSampleGenerator() {
        return new AppUser()
            .id(longCount.incrementAndGet())
            .firstName(UUID.randomUUID().toString())
            .lastName(UUID.randomUUID().toString())
            .password(UUID.randomUUID().toString())
            .phoneNumber(UUID.randomUUID().toString())
            .address(UUID.randomUUID().toString());
    }
}
