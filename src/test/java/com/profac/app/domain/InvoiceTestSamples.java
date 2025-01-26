package com.profac.app.domain;

import java.util.Random;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicLong;

public class InvoiceTestSamples {

    private static final Random random = new Random();
    private static final AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    public static Invoice getInvoiceSample1() {
        return new Invoice().id(1L).invoiceNumber(1L).customer("customer1").invoiceDate("invoiceDate1");
    }

    public static Invoice getInvoiceSample2() {
        return new Invoice().id(2L).invoiceNumber(2L).customer("customer2").invoiceDate("invoiceDate2");
    }

    public static Invoice getInvoiceRandomSampleGenerator() {
        return new Invoice()
            .id(longCount.incrementAndGet())
            .invoiceNumber(longCount.incrementAndGet())
            .customer(UUID.randomUUID().toString())
            .invoiceDate(UUID.randomUUID().toString());
    }
}
