package com.adera.aderapos.invoice.entities.enums;

/**
 * Enumeration representing the various statuses an invoice can have.
 */
public enum InvoiceStatus {
    CREATED,
    SIGNED,
    QR_GENERATED,
    SUBMITTED,
    ACCEPTED,
    REJECTED,
    CANCELLED
}
