package com.adera.aderapos.invoice.controller;

import com.adera.aderapos.invoice.dtos.InvoiceDTO;
import com.adera.aderapos.invoice.entities.Invoice;
import com.adera.aderapos.invoice.mapper.InvoiceMapper;
import com.adera.aderapos.invoice.services.InvoiceService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/invoices")
@RequiredArgsConstructor
public class InvoiceController {
    private final InvoiceService invoiceService;
    private final InvoiceMapper invoiceMapper;

    @PostMapping
    public ResponseEntity<InvoiceDTO> createInvoice(@RequestParam UUID saleId, @RequestParam UUID shopId) {
        Invoice invoice = invoiceService.createInvoice(saleId, shopId);
        return ResponseEntity.ok(invoiceMapper.toDto(invoice));
    }

    @GetMapping("/{id}")
    public ResponseEntity<InvoiceDTO> getInvoiceById(@PathVariable UUID id) {
        Invoice invoice = invoiceService.getInvoiceById(id);
        return ResponseEntity.ok(invoiceMapper.toDto(invoice));
    }

    @GetMapping("/by-sale/{saleId}")
    public ResponseEntity<InvoiceDTO> getInvoiceBySaleId(@PathVariable UUID saleId) {
        Invoice invoice = invoiceService.getInvoiceBySaleId(saleId);
        return ResponseEntity.ok(invoiceMapper.toDto(invoice));
    }

    @GetMapping("/by-number/{invoiceNumber}")
    public ResponseEntity<InvoiceDTO> getInvoiceByNumber(@PathVariable String invoiceNumber) {
        Invoice invoice = invoiceService.getInvoiceByNumber(invoiceNumber);
        return ResponseEntity.ok(invoiceMapper.toDto(invoice));
    }

    @GetMapping
    public ResponseEntity<List<InvoiceDTO>> getAllInvoices() {
        List<Invoice> invoices = invoiceService.getAllInvoices();
        return ResponseEntity.ok(invoices.stream().map(invoiceMapper::toDto).toList());
    }
}
