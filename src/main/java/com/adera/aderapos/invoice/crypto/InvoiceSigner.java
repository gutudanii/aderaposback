package com.adera.aderapos.invoice.crypto;

import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

import java.util.Base64;

@Service
@Profile("!prod") // ← NOT active in prod
public class InvoiceSigner {

    public String sign(String hash) {
        // TODO (PROD):
        // Replace with RSA private key signing (SHA256withRSA)
        return Base64.getEncoder()
                .encodeToString(("DUMMY-SIGNATURE::" + hash).getBytes());
    }
}
