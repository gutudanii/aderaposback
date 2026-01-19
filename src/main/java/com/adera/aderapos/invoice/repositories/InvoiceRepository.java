package com.adera.aderapos.invoice.repositories;

import com.adera.aderapos.invoice.entities.Invoice;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

/**
 * Repository interface for Invoice entity.
 */
public interface InvoiceRepository extends JpaRepository<Invoice, UUID> {

    Optional<Invoice> findBySaleId(UUID saleId);

    Optional<Invoice> findByInvoiceNumber(String invoiceNumber);
}
