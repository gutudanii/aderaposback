package com.adera.aderapos.invoice.crypto;

import com.adera.aderapos.invoice.entities.Invoice;
import com.adera.aderapos.invoice.entities.InvoiceLine;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Component;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Component
public class CanonicalJsonWriter {

    private final ObjectMapper mapper = new ObjectMapper();

    public String write(Invoice invoice, List<InvoiceLine> lines) {
        try {
            Map<String, Object> root = new LinkedHashMap<>();
            root.put("invoiceNumber", invoice.getInvoiceNumber());
            root.put("issuedAt", invoice.getIssuedAt().toString());
            root.put("netAmount", invoice.getNetAmount());
            root.put("vatAmount", invoice.getVatAmount());
            root.put("surtaxAmount", invoice.getSurtaxAmount());
            root.put("grossAmount", invoice.getGrossAmount());

            return mapper.writeValueAsString(root);
        } catch (Exception e) {
            throw new RuntimeException("Canonical JSON failed", e);
        }
    }
}
