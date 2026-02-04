package com.adera.aderapos.identity.mapper;

import com.adera.aderapos.identity.dtos.ShopDTO;
import com.adera.aderapos.identity.entities.Merchant;
import com.adera.aderapos.identity.entities.Shop;

/**
 * Mapper class for converting between Shop entities and ShopDTOs.
 */
public class ShopMapper {
    /**
     * Converts a Shop entity to a ShopDTO.
     *
     * @param shop the Shop entity to convert
     * @return the corresponding ShopDTO
     */
    public static ShopDTO toDTO(Shop shop) {
        return ShopDTO.builder()
                .shopId(shop.getId())
                .name(shop.getName())
                .merchantId(shop.getMerchant() != null ? shop.getMerchant().getId() : null)
                .build();
    }

    /**
     * Converts a ShopDTO to a Shop entity.
     *
     * @param dto the ShopDTO to convert
     * @param merchant the Merchant entity associated with the shop
     * @return the corresponding Shop entity
     */
    public static Shop toEntity(ShopDTO dto, Merchant merchant) {
        Shop shop = new Shop();
        shop.setName(dto.getName());
        shop.setMerchant(merchant);
        return shop;
    }
}

