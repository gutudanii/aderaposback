package com.adera.aderapos.invoice.qr;

import com.adera.aderapos.invoice.entities.Invoice;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class QrPayloadBuilder {

    @Value("${aderapos.qr.base-url}")
    private String baseUrl;

    public QrPayload build(Invoice invoice) {
        return QrPayload.builder()
                .invoiceNumber(invoice.getInvoiceNumber())
                .invoiceHash(invoice.getInvoiceHash())
                .merchantTin(invoice.getShop().getMerchant().getTin())
                .verificationUrl(baseUrl + "/" + invoice.getInvoiceNumber())
                .build();
    }
}
