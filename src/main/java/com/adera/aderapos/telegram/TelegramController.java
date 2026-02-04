package com.adera.aderapos.telegram;

import com.adera.aderapos.identity.entities.AppUser;
import com.adera.aderapos.identity.repositories.AppUserRepository;
import com.adera.aderapos.sales.dtos.SaleDTO;
import com.adera.aderapos.sales.services.SaleService;
import com.adera.aderapos.invoice.dtos.InvoiceDTO;
import com.adera.aderapos.invoice.services.InvoiceService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@CrossOrigin(origins = {"http://localhost:3000", "https://aderapos.netlify.app/"})
@RestController
@RequestMapping("/api/telegram")
@RequiredArgsConstructor
public class TelegramController {

    private final AppUserRepository userRepository;
    private final SaleService saleService;
    private final InvoiceService invoiceService;

    // Bind Telegram ID to AppUser
    @PostMapping("/bind")
    public ResponseEntity<String> bindTelegram(@RequestParam String telegramId, @RequestParam String userId) {
        AppUser user = userRepository.findById(UUID.fromString(userId))
                .orElseThrow(() -> new RuntimeException("User not found"));
        user.setTelegramId(telegramId);
        userRepository.save(user);
        return ResponseEntity.ok("Telegram ID bound to user successfully");
    }

    // Create sale from Telegram WebApp
    @PostMapping("/sale")
    public ResponseEntity<?> createSaleFromTelegram(@RequestBody SaleDTO saleDTO, @RequestHeader("X-Telegram-Id") String telegramId) {
        AppUser user = userRepository.findByTelegramId(telegramId)
                .orElseThrow(() -> new RuntimeException("Unauthorized Telegram user"));
        saleDTO.setUserId(user.getId());
        return ResponseEntity.ok(saleService.createSale(saleDTO));
    }

    // List invoices for a Telegram user
    @GetMapping("/invoices")
    public ResponseEntity<List<InvoiceDTO>> getInvoicesForTelegramUser(@RequestHeader("X-Telegram-Id") String telegramId) {
        AppUser user = userRepository.findByTelegramId(telegramId)
                .orElseThrow(() -> new RuntimeException("Unauthorized Telegram user"));
        List<InvoiceDTO> invoices = invoiceService.getInvoicesByUserId(user.getId());
        return ResponseEntity.ok(invoices);
    }

    // TODO: Add endpoints for offline queue and notifications as needed
}

