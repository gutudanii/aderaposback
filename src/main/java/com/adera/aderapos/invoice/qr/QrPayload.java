package com.adera.aderapos.invoice.qr;

import lombok.Builder;
import lombok.Value;

@Value
@Builder
public class QrPayload {

    String invoiceNumber;
    String invoiceHash;
    String merchantTin;
    String verificationUrl;
}
