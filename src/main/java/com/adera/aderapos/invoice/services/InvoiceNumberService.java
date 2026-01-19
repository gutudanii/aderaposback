package com.adera.aderapos.invoice.services;

import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicLong;

/**
 * Service for generating unique invoice numbers.
 */
@Service
public class InvoiceNumberService {

    private final AtomicLong sequence = new AtomicLong(1);

    /**
     * Generates the next unique invoice number for a given shop.
     *
     * @param shopId the ID of the shop
     * @return the generated invoice number
     */
    public String nextInvoiceNumber(UUID shopId) {
        return "INV-" + shopId + "-" + LocalDate.now() + "-" + sequence.getAndIncrement();
    }
}
