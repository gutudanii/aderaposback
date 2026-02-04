package com.adera.aderapos.product.services;

import com.adera.aderapos.identity.entities.Shop;
import com.adera.aderapos.identity.repositories.ShopRepository;
import com.adera.aderapos.product.dtos.InventoryDTO;
import com.adera.aderapos.product.dtos.ProductDTO;
import com.adera.aderapos.product.dtos.ProductRequestDTO;
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
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Service
@RequiredArgsConstructor
public class ProductServiceImpl implements ProductService {

    private final ProductRepository productRepository;
    private final InventoryRepository inventoryRepository;
    private final ShopRepository shopRepository;
    private final ProductMapper productMapper;
    private final AuditService auditService;

    private static final Logger logger = LoggerFactory.getLogger(ProductServiceImpl.class);


    /**
     * Creates a new product using the ProductRequestDTO.
     * This method is used for clean product creation requests, omitting id and audit fields.
     *
     * @param dto the product request DTO
     * @return the created product DTO
     */
    @Transactional
    public ProductDTO createProduct(ProductRequestDTO dto) {
        logger.info("[ProductServiceImpl] createProduct called with: {}", dto);
        if (dto == null) {
            logger.error("ProductRequestDTO must not be null");
            throw new IllegalArgumentException("ProductRequestDTO must not be null");
        }
        UUID shopId = dto.getShopId() != null ? dto.getShopId() : SecurityUtils.getCurrentShopId();
        if (shopId == null) {
            logger.error("Shop ID is required");
            throw new RuntimeException("Shop ID is required");
        }
        Shop shop = shopRepository.findById(shopId)
                .orElseThrow(() -> {
                    logger.error("Shop not found for id: {}", shopId);
                    return new RuntimeException("Shop not found");
                });
        // SKU uniqueness check per shop
        if (productRepository.findByShop(shop).stream().anyMatch(p -> p.getSku().equals(dto.getSku()))) {
            logger.error("SKU '{}' already exists in shop {}", dto.getSku(), shopId);
            throw new RuntimeException("SKU already exists in this shop");
        }
        Product product = Product.builder()
                .shop(shop)
                .name(dto.getName())
                .sku(dto.getSku())
                .description(dto.getDescription())
                .unitPrice(dto.getUnitPrice())
                .active(dto.getActive() != null ? dto.getActive() : Boolean.TRUE)
                .createdAt(java.time.Instant.now())
                .createdBy(SecurityUtils.getCurrentUserId())
                .updatedAt(java.time.Instant.now())
                .updatedBy(SecurityUtils.getCurrentUserId())
                .build();
        logger.debug("Saving new product: name={}, sku={}, shopId={}", product.getName(), product.getSku(), product.getShop() != null ? product.getShop().getId() : null);
        Product saved = productRepository.save(product);
        logger.info("Product saved with id: {}", saved.getId());
        // Create Inventory for new product
        Inventory inventory = Inventory.builder()
                .product(saved)
                .quantity(dto.getInitialQuantity() != null ? dto.getInitialQuantity() : 0)
                .reserved(0)
                .createdAt(java.time.Instant.now())
                .createdBy(SecurityUtils.getCurrentUserId())
                .updatedAt(java.time.Instant.now())
                .updatedBy(SecurityUtils.getCurrentUserId())
                .build();
        inventoryRepository.save(inventory);
        logger.info("Inventory created for product id: {}", saved.getId());
        auditService.log(
            AuditAction.CREATE,
            AuditEntityType.PRODUCT,
            saved.getId(),
            UUID.randomUUID(),
            "null",
            AuditSeverity.LOW,
            "Product created"
        );
        logger.info("Audit log created for product id: {}", saved.getId());
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
        Inventory inventory = inventoryRepository.findByProduct(product)
                .orElseThrow(() -> new RuntimeException("Inventory not found for product"));
        ProductDTO productDTO = productMapper.toDto(product);
        productDTO.setQuantity(inventory.getQuantity());
        return productDTO;
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
        List<ProductDTO> productDTOs = products.stream().map(product -> {
            Inventory inventory = inventoryRepository.findByProduct(product)
                    .orElseThrow(() -> new RuntimeException("Inventory not found for product"));
            ProductDTO dto = productMapper.toDto(product);
            dto.setQuantity(inventory.getQuantity());
            return dto;
        }).collect(Collectors.toList());
        return productDTOs;
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

        // Handle inventory quantity update if provided
        if (dto.getQuantity() != 0) {
            Inventory inventory = inventoryRepository.findByProduct(product)
                    .orElseThrow(() -> new RuntimeException("Inventory not found for product"));
            int newQuantity = dto.getQuantity();
            if (newQuantity < 0) {
                throw new RuntimeException("Quantity cannot be negative");
            }
            int oldQuantity = inventory.getQuantity();
            inventory.setQuantity(newQuantity);
            inventory.setUpdatedAt(java.time.Instant.now());
            inventory.setUpdatedBy(currentUser);
            inventoryRepository.save(inventory);
            logger.info("Inventory updated for product id {}: {} -> {}", product.getId(), oldQuantity, newQuantity);
            auditService.log(
                AuditAction.UPDATE,
                AuditEntityType.PRODUCT,
                updated.getId(),
                currentUser != null ? UUID.fromString(currentUser) : null,
                null,
                AuditSeverity.LOW,
                "Product inventory updated"
            );
        }

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
