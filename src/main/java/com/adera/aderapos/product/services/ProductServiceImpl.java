package com.adera.aderapos.product.services;

import com.adera.aderapos.identity.entities.Shop;
import com.adera.aderapos.identity.repositories.ShopRepository;
import com.adera.aderapos.product.dtos.InventoryDTO;
import com.adera.aderapos.product.dtos.ProductDTO;
import com.adera.aderapos.product.entities.Inventory;
import com.adera.aderapos.product.entities.Product;
import com.adera.aderapos.product.repositories.InventoryRepository;
import com.adera.aderapos.product.repositories.ProductRepository;
import com.adera.aderapos.product.mapper.ProductMapper;
import com.adera.aderapos.security.utils.SecurityUtils;
import com.adera.aderapos.audit.services.AuditService;
import com.adera.aderapos.audit.entities.enums.AuditAction;
import com.adera.aderapos.audit.entities.enums.AuditEntityType;
import com.adera.aderapos.audit.entities.enums.AuditSeverity;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ProductServiceImpl implements ProductService {

    private final ProductRepository productRepository;
    private final InventoryRepository inventoryRepository;
    private final ShopRepository shopRepository;
    private final ProductMapper productMapper;
    private final AuditService auditService;

    /**
     * Creates or updates a product.
     *
     * @param dto the product DTO
     * @return the created or updated product DTO
     */
    @Transactional
    @Override
    public ProductDTO createOrUpdateProduct(ProductDTO dto) {
        UUID shopId = dto.getShopId() != null ? dto.getShopId() : SecurityUtils.getCurrentShopId();
        if (shopId == null) throw new RuntimeException("Shop ID is required");
        Shop shop = shopRepository.findById(shopId)
                .orElseThrow(() -> new RuntimeException("Shop not found"));
        Product product = productMapper.toEntity(dto);
        product.setShop(shop);
        String currentUser = SecurityUtils.getCurrentUserId();
        if (currentUser == null) throw new RuntimeException("User not authenticated");
        if (product.getCreatedAt() == null) {
            product.setCreatedAt(java.time.Instant.now());
            product.setCreatedBy(currentUser);
        }
        product.setUpdatedAt(java.time.Instant.now());
        product.setUpdatedBy(currentUser);
        Product saved = productRepository.save(product);
        auditService.log(
            AuditAction.CREATE,
            AuditEntityType.PRODUCT,
            saved.getId(),
            UUID.fromString(currentUser),
            null, // No user role available
            AuditSeverity.LOW,
            "Product created"
        );
        return productMapper.toDto(saved);
    }

    /**
     * Retrieves a product by its ID.
     *
     * @param id the product ID
     * @return the product DTO
     */
    @Override
    public ProductDTO getProduct(UUID id) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Product not found"));
        return productMapper.toDto(product);
    }

    /**
     * Retrieves products by shop ID.
     *
     * @param shopId the shop ID
     * @return the list of product DTOs
     */
    @Override
    public List<ProductDTO> getProductsByShop(UUID shopId) {
        Shop shop = shopRepository.findById(shopId)
                .orElseThrow(() -> new RuntimeException("Shop not found"));
        List<Product> products = productRepository.findByShop(shop);
        return productMapper.toProductDtoList(products);
    }

    /**
     * Retrieves all products.
     *
     * @return the list of all product DTOs
     */
    @Override
    public List<ProductDTO> getAllProducts() {
        List<Product> products = productRepository.findAll();
        return productMapper.toProductDtoList(products);
    }

    /**
     * Updates the inventory for a product.
     *
     * @param productId      the product ID
     * @param quantityChange the change in quantity (positive or negative)
     * @return the updated inventory DTO
     */
    @Transactional
    @Override
    public InventoryDTO updateInventory(UUID productId, int quantityChange) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new RuntimeException("Product not found"));
        Inventory inventory = inventoryRepository.findByProduct(product)
                .orElseGet(() -> Inventory.builder()
                        .product(product)
                        .quantity(0)
                        .reserved(0)
                        .build());
        int newQuantity = inventory.getQuantity() + quantityChange;
        if (newQuantity < 0) {
            throw new RuntimeException("Insufficient stock");
        }
        String currentUser = SecurityUtils.getCurrentUserId();
        inventory.setQuantity(newQuantity);
        if (inventory.getCreatedAt() == null) {
            inventory.setCreatedAt(java.time.Instant.now());
            inventory.setCreatedBy(currentUser);
        }
        inventory.setUpdatedAt(java.time.Instant.now());
        inventory.setUpdatedBy(currentUser);
        Inventory saved = inventoryRepository.save(inventory);
        return productMapper.toDto(saved);
    }

    /**
     * Deletes a product by its ID.
     *
     * @param id the product ID
     */
    @Transactional
    @Override
    public void deleteProduct(UUID id) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Product not found"));
        productRepository.delete(product);
        String currentUser = SecurityUtils.getCurrentUserId();
        auditService.log(
            AuditAction.DELETE,
            AuditEntityType.PRODUCT,
            product.getId(),
            currentUser != null ? UUID.fromString(currentUser) : null,
            null, // No user role available
            AuditSeverity.LOW,
            "Product deleted"
        );
    }

    /**
     * Updates a product.
     *
     * @param id  the product ID
     * @param dto the product DTO
     * @return the updated product DTO
     */
    @Transactional
    @Override
    public ProductDTO updateProduct(UUID id, ProductDTO dto) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Product not found"));
        // Manual update (since updateEntityFromDto does not exist)
        if (dto.getName() != null) product.setName(dto.getName());
        if (dto.getDescription() != null) product.setDescription(dto.getDescription());
        if (dto.getUnitPrice() != null) product.setUnitPrice(dto.getUnitPrice());
        if (dto.getShopId() != null) {
            Shop shop = shopRepository.findById(dto.getShopId())
                .orElseThrow(() -> new RuntimeException("Shop not found"));
            product.setShop(shop);
        }
        product.setUpdatedAt(java.time.Instant.now());
        String currentUser = SecurityUtils.getCurrentUserId();
        product.setUpdatedBy(currentUser);
        Product updated = productRepository.save(product);
        auditService.log(
            AuditAction.UPDATE,
            AuditEntityType.PRODUCT,
            updated.getId(),
            currentUser != null ? UUID.fromString(currentUser) : null,
            null, // No user role available
            AuditSeverity.LOW,
            "Product updated"
        );
        return productMapper.toDto(updated);
    }
}
