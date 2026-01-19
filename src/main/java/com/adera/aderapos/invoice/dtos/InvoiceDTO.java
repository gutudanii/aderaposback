package com.adera.aderapos.invoice.dtos;

import com.adera.aderapos.invoice.entities.enums.InvoiceStatus;
import lombok.*;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.UUID;

/**
 * Data Transfer Object for Invoice entity.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class InvoiceDTO {

    private String invoiceNumber;
    private UUID saleId;
    private InvoiceStatus status;

    private BigDecimal netAmount;
    private BigDecimal vatAmount;
    private BigDecimal surtaxAmount;
    private BigDecimal grossAmount;

    private Instant issuedAt;
    private List<InvoiceLineDTO> lines;

    private Object qrPayload;

    private String createdBy;
    private String updatedBy;
    private Instant createdAt;
    private Instant updatedAt;
    private Instant deletedAt;
    private String deletedBy;
}
