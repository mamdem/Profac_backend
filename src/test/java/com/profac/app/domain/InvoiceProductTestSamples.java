package com.profac.app.domain;

import java.util.Random;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

public class InvoiceProductTestSamples {

    private static final Random random = new Random();
    private static final AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));
    private static final AtomicInteger intCount = new AtomicInteger(random.nextInt() + (2 * Short.MAX_VALUE));

    public static InvoiceProduct getInvoiceProductSample1() {
        return new InvoiceProduct().id(1L).quantity(1);
    }

    public static InvoiceProduct getInvoiceProductSample2() {
        return new InvoiceProduct().id(2L).quantity(2);
    }

    public static InvoiceProduct getInvoiceProductRandomSampleGenerator() {
        return new InvoiceProduct().id(longCount.incrementAndGet()).quantity(intCount.incrementAndGet());
    }
}
