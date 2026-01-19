package com.adera.aderapos.invoice.services;

import com.adera.aderapos.identity.entities.Shop;
import com.adera.aderapos.identity.repositories.ShopRepository;
import com.adera.aderapos.invoice.crypto.CanonicalJsonWriter;
import com.adera.aderapos.invoice.crypto.InvoiceHashService;
import com.adera.aderapos.invoice.crypto.InvoiceSigner;
import com.adera.aderapos.invoice.entities.*;
import com.adera.aderapos.invoice.entities.enums.*;
import com.adera.aderapos.invoice.qr.QrGenerator;
import com.adera.aderapos.invoice.qr.QrPayload;
import com.adera.aderapos.invoice.qr.QrPayloadBuilder;
import com.adera.aderapos.invoice.repositories.InvoiceRepository;
import com.adera.aderapos.sales.entities.*;
import com.adera.aderapos.sales.repositories.SaleRepository;
import com.adera.aderapos.product.repositories.ProductRepository;
import com.adera.aderapos.audit.services.AuditService;
import com.adera.aderapos.audit.entities.enums.AuditAction;
import com.adera.aderapos.audit.entities.enums.AuditEntityType;
import com.adera.aderapos.audit.entities.enums.AuditSeverity;
import com.adera.aderapos.invoice.dtos.InvoiceDTO;
import com.adera.aderapos.invoice.mapper.InvoiceMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class InvoiceService {

    private final SaleRepository saleRepository;
    private final ShopRepository shopRepository;
    private final InvoiceRepository invoiceRepository;
    private final InvoiceNumberService numberService;
    private final CanonicalJsonWriter canonicalWriter;
    private final InvoiceHashService hashService;
    private final InvoiceSigner signer;
    private final ProductRepository productRepository;
    private final QrPayloadBuilder qrPayloadBuilder;
    private final QrGenerator qrGenerator;
    private final AuditService auditService;
    private final InvoiceMapper invoiceMapper;

    @Transactional
    public Invoice createInvoice(UUID saleId, UUID shopId) {

        Sale sale = saleRepository.findById(saleId)
                .orElseThrow(() -> new RuntimeException("Sale not found"));

        Shop shop = shopRepository.findById(shopId)
                .orElseThrow(() -> new RuntimeException("Shop not found"));

        BigDecimal vat = sale.getTotalAmount().multiply(new BigDecimal("0.15"));
        BigDecimal surtax = sale.getTotalAmount().multiply(new BigDecimal("0.01"));
        BigDecimal gross = sale.getTotalAmount().add(vat).add(surtax);

        Invoice invoice = Invoice.builder()
                .invoiceNumber(numberService.nextInvoiceNumber(shopId))
                .shop(shop)
                .saleId(saleId)
                .type(InvoiceType.NORMAL)
                .status(InvoiceStatus.CREATED)
                .netAmount(sale.getTotalAmount())
                .vatAmount(vat)
                .surtaxAmount(surtax)
                .grossAmount(gross)
                .issuedAt(Instant.now())
                .build();

        List<InvoiceLine> lines = sale.getSaleItems().stream()
                .map(item -> InvoiceLine.builder()
                        .invoice(invoice)
                        .productName(productRepository.findById(item.getProductId())
                            .map(p -> p.getName()).orElse("Unknown"))
                        .quantity(item.getQuantity())
                        .unitPrice(item.getUnitPrice())
                        .lineTotal(item.getTotalPrice())
                        .build())
                .toList();

        String canonical = canonicalWriter.write(invoice, lines);
        String hash = hashService.hash(canonical);
        String signature = signer.sign(hash);

        invoice.setCanonicalJson(canonical);
        invoice.setInvoiceHash(hash);
        invoice.setSignature(signature);
        invoice.markStatus(InvoiceStatus.SIGNED);
        invoice.setLines(lines);

        QrPayload payload = qrPayloadBuilder.build(invoice);
        String qrJson = qrGenerator.generate(payload);

        invoice.setQrPayload(qrJson);
        invoiceRepository.save(invoice);
        auditService.log(
            AuditAction.CREATE,
            AuditEntityType.INVOICE,
            invoice.getId(),
            null,
            null,
            AuditSeverity.LOW,
            "Invoice created"
        );
        return invoice;
    }

    public Invoice getInvoiceById(UUID id) {
        return invoiceRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Invoice not found"));
    }

    public Invoice getInvoiceBySaleId(UUID saleId) {
        return invoiceRepository.findBySaleId(saleId)
                .orElseThrow(() -> new RuntimeException("Invoice not found for sale"));
    }

    public Invoice getInvoiceByNumber(String invoiceNumber) {
        return invoiceRepository.findByInvoiceNumber(invoiceNumber)
                .orElseThrow(() -> new RuntimeException("Invoice not found for number"));
    }

    public List<Invoice> getAllInvoices() {
        return invoiceRepository.findAll();
    }

    public List<InvoiceDTO> getInvoicesByUserId(UUID userId) {
        List<Sale> sales = saleRepository.findByUserId(userId);
        List<UUID> saleIds = sales.stream().map(Sale::getId).toList();
        List<Invoice> invoices = invoiceRepository.findAll().stream()
            .filter(inv -> saleIds.contains(inv.getSaleId()))
            .toList();
        return invoices.stream().map(invoiceMapper::toDto).toList();
    }
}
