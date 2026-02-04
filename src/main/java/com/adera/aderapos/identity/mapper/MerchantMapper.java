package com.adera.aderapos.identity.mapper;

import com.adera.aderapos.identity.dtos.MerchantDTO;
import com.adera.aderapos.identity.entities.Merchant;

/**
 * Mapper class for converting between Merchant entities and MerchantDTOs.
 */
public class MerchantMapper {

    /**
     * Converts a Merchant entity to a MerchantDTO.
     *
     * @param merchant the Merchant entity to convert
     * @return the corresponding MerchantDTO
     */
    public static MerchantDTO toDTO(Merchant merchant) {
        return MerchantDTO.builder()
                .merchantId(merchant.getId())
                .name(merchant.getName())
                .tin(merchant.getTin())
                .email(merchant.getEmail())
                .build();
    }

    /**
     * Converts a MerchantDTO to a Merchant entity.
     *
     * @param dto the MerchantDTO to convert
     * @return the corresponding Merchant entity
     */
    public static Merchant toEntity(MerchantDTO dto) {
        Merchant merchant = new Merchant();
        merchant.setName(dto.getName());
        merchant.setTin(dto.getTin());
        merchant.setEmail(dto.getEmail());
        return merchant;
    }
}

