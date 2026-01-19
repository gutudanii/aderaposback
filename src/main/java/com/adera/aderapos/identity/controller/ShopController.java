package com.adera.aderapos.identity.controller;

import com.adera.aderapos.identity.dtos.MerchantDTO;
import com.adera.aderapos.identity.dtos.ShopDTO;
import com.adera.aderapos.identity.services.IdentityService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/shops")
@RequiredArgsConstructor
public class ShopController {
    private final IdentityService identityService;

    @PostMapping
    public ResponseEntity<ShopDTO> createShop(@RequestBody ShopDTO dto) {
        return ResponseEntity.ok(identityService.createShop(dto));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ShopDTO> getShop(@PathVariable UUID id) {
        return ResponseEntity.ok(identityService.getShop(id));
    }

    @GetMapping
    public ResponseEntity<List<ShopDTO>> getAllShops() {
        return ResponseEntity.ok(identityService.getAllShops());
    }

    @GetMapping("/by-merchant/{merchantId}")
    public ResponseEntity<List<ShopDTO>> getShopsByMerchant(@PathVariable UUID merchantId) {
        return ResponseEntity.ok(identityService.getShopsByMerchantDTO(merchantId));
    }

    @PostMapping("/merchant")
    public ResponseEntity<MerchantDTO> createMerchant(@RequestBody MerchantDTO dto) {
        return ResponseEntity.ok(identityService.createMerchant(dto));
    }

    @GetMapping("/merchant/{tin}")
    public ResponseEntity<MerchantDTO> getMerchantByTin(@PathVariable String tin) {
        return ResponseEntity.ok(identityService.getMerchantByTinDTO(tin));
    }

}