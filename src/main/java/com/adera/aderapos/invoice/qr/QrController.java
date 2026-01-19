package com.adera.aderapos.invoice.qr;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/verify")
@RequiredArgsConstructor
public class QrController {

    private final QrVerifierService verifierService;

    @GetMapping("/{invoiceNumber}")
    public ResponseEntity<?> verify(
            @PathVariable String invoiceNumber,
            @RequestParam("hash") String hash) {

        boolean valid = verifierService.verify(invoiceNumber, hash);

        return ResponseEntity.ok(new VerificationResponse(valid));
    }

    record VerificationResponse(boolean valid) {}
}
