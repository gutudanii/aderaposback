package com.adera.aderapos.sales.controller;

import com.adera.aderapos.sales.services.SaleService;
import com.adera.aderapos.sales.dtos.SaleItemDTO;
import com.adera.aderapos.sales.dtos.SaleDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@CrossOrigin(origins = {"http://localhost:3000", "https://aderapos.netlify.app/"})
@RestController
@RequestMapping("/api/sales")
@RequiredArgsConstructor
public class SaleController {
    private final SaleService saleService;

    @PostMapping
    public ResponseEntity<SaleDTO> createSale(@RequestBody SaleDTO dto) {
        return ResponseEntity.ok(saleService.createSale(dto));
    }

    @GetMapping("/{id}")
    public ResponseEntity<SaleDTO> getSaleById(@PathVariable UUID id) {
        return ResponseEntity.ok(saleService.getSaleById(id));
    }

    @GetMapping("/shop/{shopId}")
    public ResponseEntity<List<SaleDTO>> getSalesByShop(@PathVariable UUID shopId) {
        return ResponseEntity.ok(saleService.getSalesByShop(shopId));
    }

    @PostMapping("/{saleId}/items")
    public ResponseEntity<Void> addSaleItem(@PathVariable UUID saleId, @RequestBody SaleItemDTO itemDTO) {
        saleService.addSaleItem(saleId, itemDTO);
        return ResponseEntity.ok().build();
    }

    @GetMapping
    public ResponseEntity<List<SaleDTO>> getAllSales() {
        return ResponseEntity.ok(saleService.getAllSales());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteSale(@PathVariable UUID id) {
        saleService.deleteSale(id);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/{id}")
    public ResponseEntity<SaleDTO> updateSale(@PathVariable UUID id, @RequestBody SaleDTO dto) {
        return ResponseEntity.ok(saleService.updateSale(id, dto));
    }

    @GetMapping("/{saleId}/items")
    public ResponseEntity<List<SaleItemDTO>> getSaleItems(@PathVariable UUID saleId) {
        return ResponseEntity.ok(saleService.getSaleItems(saleId));
    }
}
