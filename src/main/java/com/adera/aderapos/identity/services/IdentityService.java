package com.adera.aderapos.identity.services;

import com.adera.aderapos.identity.dtos.MerchantDTO;
import com.adera.aderapos.identity.dtos.ShopDTO;
import com.adera.aderapos.identity.dtos.AppUserDTO;
import com.adera.aderapos.identity.entities.TelegramBinding;
import com.adera.aderapos.identity.entities.AppUser;
import java.util.List;
import java.util.UUID;

/**
 * Service interface for managing identity-related operations such as merchants, shops, users, and Telegram bindings.
 */
public interface IdentityService {
    /**
     * Creates a new merchant.
     *
     * @param merchantDTO the DTO containing merchant details
     * @return the created MerchantDTO
     */
    MerchantDTO createMerchant(MerchantDTO merchantDTO);

    /**
     * Retrieves a merchant by its TIN.
     *
     * @param tin the TIN of the merchant
     * @return the MerchantDTO corresponding to the TIN
     */
    MerchantDTO getMerchantByTinDTO(String tin);

    /**
     * Creates a new shop.
     *
     * @param shopDTO the DTO containing shop details
     * @return the created ShopDTO
     */
    ShopDTO createShop(ShopDTO shopDTO);

    /**
     * Retrieves shops by merchant ID.
     *
     * @param merchantId the UUID of the merchant
     * @return a list of ShopDTOs associated with the merchant
     */
    List<ShopDTO> getShopsByMerchantDTO(UUID merchantId);

    /**
     * Creates a new application user.
     *
     * @param userDTO the DTO containing user details
     * @return the created AppUserDTO
     */
    AppUserDTO createUser(AppUserDTO userDTO);

    /**
     * Retrieves a user by username.
     *
     * @param username the username of the user
     * @return the AppUserDTO corresponding to the username
     */
    AppUserDTO getUserByUsernameDTO(String username);

    /**
     * Binds a Telegram user ID to an application user.
     *
     * @param user the application user
     * @param telegramUserId the Telegram user ID to bind
     * @return the created TelegramBinding entity
     */
    TelegramBinding bindTelegram(AppUser user, String telegramUserId);

    /**
     * Retrieves a user by ID.
     * @param id the UUID of the user
     * @return the AppUserDTO corresponding to the ID
     */
    AppUserDTO getUser(UUID id);

    /**
     * Retrieves all users.
     * @return a list of AppUserDTOs
     */
    List<AppUserDTO> getAllUsers();

    /**
     * Retrieves a shop by ID.
     * @param id the UUID of the shop
     * @return the ShopDTO corresponding to the ID
     */
    ShopDTO getShop(UUID id);

    /**
     * Retrieves all shops.
     * @return a list of ShopDTOs
     */
    List<ShopDTO> getAllShops();
}
