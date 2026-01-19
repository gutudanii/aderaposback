package com.adera.aderapos.product.controller;

import com.adera.aderapos.product.dtos.InventoryDTO;
import com.adera.aderapos.product.dtos.ProductDTO;
import com.adera.aderapos.product.services.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/products")
@RequiredArgsConstructor
public class ProductController {
    private final ProductService productService;

    @PostMapping
    public ResponseEntity<ProductDTO> createOrUpdateProduct(@RequestBody ProductDTO dto) {
        return ResponseEntity.ok(productService.createOrUpdateProduct(dto));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ProductDTO> getProduct(@PathVariable UUID id) {
        return ResponseEntity.ok(productService.getProduct(id));
    }

    @GetMapping("/shop/{shopId}")
    public ResponseEntity<List<ProductDTO>> getProductsByShop(@PathVariable UUID shopId) {
        return ResponseEntity.ok(productService.getProductsByShop(shopId));
    }

    @PatchMapping("/{productId}/inventory")
    public ResponseEntity<InventoryDTO> updateInventory(@PathVariable UUID productId, @RequestParam int quantityChange) {
        return ResponseEntity.ok(productService.updateInventory(productId, quantityChange));
    }

    @GetMapping
    public ResponseEntity<List<ProductDTO>> getAllProducts() {
        return ResponseEntity.ok(productService.getAllProducts());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteProduct(@PathVariable UUID id) {
        productService.deleteProduct(id);
        return ResponseEntity.noContent().build();
    }
}
