package com.adera.aderapos.invoice.mapper;

import com.adera.aderapos.invoice.dtos.InvoiceDTO;
import com.adera.aderapos.invoice.dtos.InvoiceLineDTO;
import com.adera.aderapos.invoice.entities.Invoice;
import com.adera.aderapos.invoice.entities.InvoiceLine;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import java.util.List;

/**
 * Mapper interface for converting between Invoice entities and DTOs.
 */
@Mapper(componentModel = "spring")
public interface InvoiceMapper {
    /**
     * Converts an Invoice entity to an InvoiceDTO.
     *
     * @param invoice the Invoice entity
     * @return the corresponding InvoiceDTO
     */
    @Mapping(source = "createdAt", target = "createdAt")
    @Mapping(source = "updatedAt", target = "updatedAt")
    @Mapping(source = "createdBy", target = "createdBy")
    @Mapping(source = "updatedBy", target = "updatedBy")
    @Mapping(source = "deletedAt", target = "deletedAt")
    @Mapping(source = "deletedBy", target = "deletedBy")
    @Mapping(source = "qrPayload", target = "qrPayload")
    InvoiceDTO toDto(Invoice invoice);

    /**
     * Converts an InvoiceLine entity to an InvoiceLineDTO.
     *
     * @param line the InvoiceLine entity
     * @return the corresponding InvoiceLineDTO
     */
    @Mapping(source = "createdAt", target = "createdAt")
    @Mapping(source = "updatedAt", target = "updatedAt")
    @Mapping(source = "createdBy", target = "createdBy")
    @Mapping(source = "updatedBy", target = "updatedBy")
    @Mapping(source = "deletedAt", target = "deletedAt")
    @Mapping(source = "deletedBy", target = "deletedBy")
    InvoiceLineDTO toDto(InvoiceLine line);

    List<InvoiceLineDTO> toLineDtoList(List<InvoiceLine> lines);
}