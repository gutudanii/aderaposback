package com.adera.aderapos.product.services;

import com.adera.aderapos.product.dtos.InventoryDTO;
import com.adera.aderapos.product.dtos.ProductDTO;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

public interface ProductService {
    ProductDTO createOrUpdateProduct(ProductDTO dto);
    ProductDTO getProduct(UUID id);
    List<ProductDTO> getProductsByShop(UUID shopId);
    InventoryDTO updateInventory(UUID productId, int quantityChange);
    List<ProductDTO> getAllProducts();
    void deleteProduct(UUID id);

    @Transactional
    ProductDTO updateProduct(UUID id, ProductDTO dto);
}
