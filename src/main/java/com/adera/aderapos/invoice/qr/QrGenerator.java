package com.adera.aderapos.invoice.qr;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import org.springframework.stereotype.Component;

@Component
public class QrGenerator {

    private final ObjectMapper mapper;

    public QrGenerator() {
        this.mapper = new ObjectMapper();
        this.mapper.configure(SerializationFeature.ORDER_MAP_ENTRIES_BY_KEYS, true);
    }

    public String generate(QrPayload payload) {
        try {
            return mapper.writeValueAsString(payload);
        } catch (Exception e) {
            throw new RuntimeException("QR generation failed", e);
        }
    }
}
