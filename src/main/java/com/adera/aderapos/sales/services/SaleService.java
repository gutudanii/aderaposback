package com.adera.aderapos.sales.services;

import com.adera.aderapos.sales.dtos.SaleDTO;
import com.adera.aderapos.sales.dtos.SaleItemDTO;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

public interface SaleService {
    /**
     * Create a new Sale.
     *
     * @param saleDTO the SaleDTO containing sale details
     * @return the created SaleDTO
     */
    @Transactional
    SaleDTO createSale(SaleDTO saleDTO);

    /**
     * Get a Sale by its ID.
     *
     * @param id the UUID of the sale
     * @return the SaleDTO
     */
    SaleDTO getSaleById(UUID id);

    /**
     * Get all Sales for a specific Shop.
     *
     * @param shopId the UUID of the shop
     * @return list of SaleDTOs
     */
    List<SaleDTO> getSalesByShop(UUID shopId);

    /**
     * Get all Sales.
     *
     * @return list of all SaleDTOs
     */
    List<SaleDTO> getAllSales();

    /**
     * Delete a Sale by its ID.
     *
     * @param id the UUID of the sale
     */
    void deleteSale(UUID id);

    /**
     * Update an existing Sale.
     *
     * @param id      the UUID of the sale
     * @param saleDTO the SaleDTO containing updated sale details
     * @return the updated SaleDTO
     */
    SaleDTO updateSale(UUID id, SaleDTO saleDTO);

    /**
     * Add a SaleItem to an existing Sale.
     *
     * @param saleId  the UUID of the sale
     * @param itemDTO the SaleItemDTO containing item details
     */
    @Transactional
    void addSaleItem(UUID saleId, SaleItemDTO itemDTO);

    /**
     * Get all SaleItems for a specific Sale.
     *
     * @param saleId the UUID of the sale
     * @return list of SaleItemDTOs
     */
    List<SaleItemDTO> getSaleItems(UUID saleId);
}
