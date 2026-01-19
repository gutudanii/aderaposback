package com.adera.aderapos.invoice.qr;

import com.adera.aderapos.invoice.entities.Invoice;
import com.adera.aderapos.invoice.repositories.InvoiceRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class QrVerifierService {

    private final InvoiceRepository invoiceRepository;

    public boolean verify(String invoiceNumber, String hashFromQr) {

        Invoice invoice = invoiceRepository.findByInvoiceNumber(invoiceNumber)
                .orElseThrow(() -> new RuntimeException("Invoice not found"));

        return invoice.getInvoiceHash().equals(hashFromQr);
    }
}
