package com.adera.aderapos.sales.services;

import com.adera.aderapos.identity.entities.AppUser;
import com.adera.aderapos.identity.entities.Shop;
import com.adera.aderapos.identity.repositories.AppUserRepository;
import com.adera.aderapos.identity.repositories.ShopRepository;
import com.adera.aderapos.product.dtos.ProductRequestDTO;
import com.adera.aderapos.product.entities.Inventory;
import com.adera.aderapos.product.entities.Product;
import com.adera.aderapos.product.repositories.InventoryRepository;
import com.adera.aderapos.product.repositories.ProductRepository;
import com.adera.aderapos.sales.dtos.SaleDTO;
import com.adera.aderapos.sales.dtos.SaleItemDTO;
import com.adera.aderapos.sales.entities.Sale;
import com.adera.aderapos.sales.entities.SaleItem;
import com.adera.aderapos.sales.entities.enums.SaleStatus;
import com.adera.aderapos.sales.mapper.SaleMapper;
import com.adera.aderapos.sales.repositories.SaleItemRepository;
import com.adera.aderapos.sales.repositories.SaleRepository;
import com.adera.aderapos.security.utils.SecurityUtils;
import com.adera.aderapos.audit.services.AuditService;
import com.adera.aderapos.audit.entities.enums.AuditAction;
import com.adera.aderapos.audit.entities.enums.AuditEntityType;
import com.adera.aderapos.audit.entities.enums.AuditSeverity;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * SaleService implementation for managing sales operations.
 */
@Service
@RequiredArgsConstructor
public class SaleServiceImpl implements SaleService{

    private static final Logger log = LoggerFactory.getLogger(SaleServiceImpl.class);

    private final SaleRepository saleRepository;
    private final SaleItemRepository saleItemRepository;
    private final ShopRepository shopRepository;
    private final AppUserRepository userRepository;
    private final SaleMapper saleMapper;
    private final AuditService auditService;
    private final InventoryRepository inventoryRepository;

    /**
     * Creates a new sale.
     * @param saleDTO the DTO containing sale details
     * @return the created SaleDTO
     */
    @Transactional
    @Override
    public SaleDTO createSale(SaleDTO saleDTO) {
        log.info("Creating sale for shopId={}, userId={}", saleDTO.getShopId(), SecurityUtils.getCurrentUserId());
//        UUID userId = UUID.fromString(SecurityUtils.getCurrentUserId());
        UUID userId = UUID.fromString("ce2c8794-c62c-4ad4-bffc-5b612078dbc4"); // Temporary for testing without security
        AppUser user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));
        Shop shop = shopRepository.findById(saleDTO.getShopId())
                .orElseThrow(() -> new RuntimeException("Shop not found"));
        log.debug("Loaded shop: {}", shop);
//        if (!shop.getId().equals(user.getShop().getId())) {
//            throw new RuntimeException("User does not belong to this shop");
//        }
        log.debug("User authorized for shop: {}", shop.getName());

        // Map DTO → Entity
        Sale sale = Sale.builder()
                .shop(shop)
                .user(user)
                .paymentMethod(saleDTO.getPaymentMethod())
                .status(SaleStatus.SALE_CREATED)
                .createdAt(Instant.now())
                .createdBy(user.getUsername())
                .updatedAt(Instant.now())
                .updatedBy(user.getUsername())
                .build();
        List<SaleItem> items = saleDTO.getSaleItems().stream()
                .map(itemDTO -> {
                    SaleItem item = saleMapper.toEntity(itemDTO);
                    item.setSale(sale);
                    item.setCreatedAt(Instant.now());
                    item.setCreatedBy(user.getUsername());
                    item.setUpdatedAt(Instant.now());
                    item.setUpdatedBy(user.getUsername());
                    return item;
                }).collect(Collectors.toList());
        sale.setSaleItems(items);
        calculateTotals(sale);
        Sale saved = saleRepository.save(sale);

        List<SaleItemDTO> saleItems = saleDTO.getSaleItems();
        for (SaleItemDTO item : saleItems) {
            Inventory inventory = inventoryRepository.findByProduct(
                    Product.builder().id(item.getProductId()).build()
            ).orElseThrow(() -> new RuntimeException("Inventory not found for product " + item.getProductId()));
            if (inventory.getQuantity() < item.getQuantity()) {
                throw new RuntimeException("Insufficient inventory for product " + item.getProductId());
            }
            inventory.setQuantity(inventory.getQuantity() - item.getQuantity());
            inventoryRepository.save(inventory);
            log.debug("Updated inventory for product {}: new quantity={}", item.getProductId(), inventory.getQuantity());
        }

        auditService.log(
            AuditAction.CREATE,
            AuditEntityType.SALE,
            saved.getId(),
            user.getId(),
            user.getRole() != null ? user.getRole().name() : null,
            AuditSeverity.LOW,
            "Sale created"
        );
        log.info("Sale created with id={}", saved.getId());
        return saleMapper.toDto(saved);
    }

    /**
     * Retrieves a sale by its ID.
     * @param id the UUID of the sale
     * @return the SaleDTO corresponding to the ID
     */
    @Override
    public SaleDTO getSaleById(UUID id) {
        Sale sale = saleRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Sale not found"));
        return saleMapper.toDto(sale);
    }

    /**
     * Retrieves sales by shop ID.
     * @param shopId the UUID of the shop
     * @return a list of SaleDTOs associated with the shop
     */
    @Override
    public List<SaleDTO> getSalesByShop(UUID shopId) {
        Shop shop = shopRepository.findById(shopId)
                .orElseThrow(() -> new RuntimeException("Shop not found"));
        List<Sale> sales = saleRepository.findByShop(shop);
        return sales.stream().map(saleMapper::toDto).collect(Collectors.toList());
    }

    /**
     * Retrieves all sales.
     * @return a list of all SaleDTOs
     */
    @Override
    public List<SaleDTO> getAllSales() {
        List<Sale> sales = saleRepository.findAll();
        return sales.stream().map(saleMapper::toDto).collect(Collectors.toList());
    }

    /**
     * Adds a sale item to an existing sale.
     * @param saleId the UUID of the sale
     * @param itemDTO the DTO containing sale item details
     */
    @Transactional
    @Override
    public void addSaleItem(UUID saleId, SaleItemDTO itemDTO) {
        Sale sale = saleRepository.findById(saleId)
                .orElseThrow(() -> new RuntimeException("Sale not found"));
        AppUser user = userRepository.findById(UUID.fromString(SecurityUtils.getCurrentUserId()))
                .orElseThrow(() -> new RuntimeException("User not found"));
        SaleItem item = saleMapper.toEntity(itemDTO);
        item.setSale(sale);
        item.setTotalPrice(item.getUnitPrice().multiply(BigDecimal.valueOf(item.getQuantity())));
        item.setCreatedAt(Instant.now());
        item.setCreatedBy(user.getUsername());
        item.setUpdatedAt(Instant.now());
        item.setUpdatedBy(user.getUsername());
        sale.getSaleItems().add(item);
        calculateTotals(sale);
        saleRepository.save(sale);
        auditService.log(
            AuditAction.UPDATE,
            AuditEntityType.SALE,
            item.getId(),
            user.getId(),
            user.getRole() != null ? user.getRole().name() : null,
            AuditSeverity.LOW,
            "Sale item added"
        );
    }

    /**
     * Deletes a sale by its ID.
     * @param id the UUID of the sale
     */
    @Transactional
    @Override
    public void deleteSale(UUID id) {
        Sale sale = saleRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Sale not found"));
        saleRepository.delete(sale);
        String currentUserId = SecurityUtils.getCurrentUserId();
        auditService.log(
            AuditAction.DELETE,
            AuditEntityType.SALE,
            sale.getId(),
            currentUserId != null ? UUID.fromString(currentUserId) : null,
            null,
            AuditSeverity.LOW,
            "Sale deleted"
        );
    }

    /**
     * Updates an existing sale.
     * @param id the UUID of the sale
     * @param saleDTO the DTO containing updated sale details
     * @return the updated SaleDTO
     */
    @Transactional
    @Override
    public SaleDTO updateSale(UUID id, SaleDTO saleDTO) {
        Sale sale = saleRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Sale not found"));
        sale.setPaymentMethod(saleDTO.getPaymentMethod());
        sale.setStatus(saleDTO.getStatus());
        sale.setUpdatedAt(Instant.now());
        sale.setUpdatedBy(SecurityUtils.getCurrentUserId());
        calculateTotals(sale);
        Sale updated = saleRepository.save(sale);
        String updateUserId = SecurityUtils.getCurrentUserId();
        auditService.log(
            AuditAction.UPDATE,
            AuditEntityType.SALE,
            updated.getId(),
            updateUserId != null ? UUID.fromString(updateUserId) : null,
            null,
            AuditSeverity.MEDIUM,
            "Sale updated"
        );
        return saleMapper.toDto(updated);
    }

    /**
     * Retrieves sale items for a given sale.
     * @param saleId the UUID of the sale
     * @return a list of SaleItemDTOs associated with the sale
     */
    @Override
    public List<SaleItemDTO> getSaleItems(UUID saleId) {
        Sale sale = saleRepository.findById(saleId)
                .orElseThrow(() -> new RuntimeException("Sale not found"));
        return sale.getSaleItems().stream().map(saleMapper::toDto).collect(Collectors.toList());
    }

    /**
     * Calculates and updates the total amount of the sale.
     * @param sale the Sale entity
     */
    public void calculateTotals(Sale sale) {
        BigDecimal total = sale.getSaleItems().stream()
                .map(SaleItem::getTotalPrice)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        sale.setTotalAmount(total);
    }
}
