package com.adera.aderapos.identity.mapper;

import com.adera.aderapos.identity.dtos.AppUserDTO;
import com.adera.aderapos.identity.entities.AppUser;
import com.adera.aderapos.identity.entities.Shop;

/**
 * Mapper class for converting between AppUser entities and AppUserDTOs.
 */
public class AppUserMapper {

    /**
     * Converts an AppUser entity to an AppUserDTO.
     *
     * @param user the AppUser entity to convert
     * @return the corresponding AppUserDTO
     */
    public static AppUserDTO toDTO(AppUser user) {
        return AppUserDTO.builder()
                .username(user.getUsername())
                .role(user.getRole())
                .shopId(user.getShop() != null ? user.getShop().getId() : null)
                .telegramId(user.getTelegramId())
                .build();
    }

    /**
     * Converts an AppUserDTO to an AppUser entity.
     *
     * @param dto the AppUserDTO to convert
     * @param shop the Shop entity associated with the user
     * @return the corresponding AppUser entity
     */
    public static AppUser toEntity(AppUserDTO dto, Shop shop) {
        AppUser user = new AppUser();
        user.setUsername(dto.getUsername());
        user.setPassword(dto.getPassword());
        user.setRole(dto.getRole());
        user.setShop(shop);
        user.setTelegramId(dto.getTelegramId());
        return user;
    }
}

