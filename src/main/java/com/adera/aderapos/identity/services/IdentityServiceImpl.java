package com.adera.aderapos.identity.services;

import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import com.adera.aderapos.identity.entities.*;
import com.adera.aderapos.identity.repositories.*;
import com.adera.aderapos.identity.dtos.MerchantDTO;
import com.adera.aderapos.identity.dtos.ShopDTO;
import com.adera.aderapos.identity.dtos.AppUserDTO;
import com.adera.aderapos.identity.mapper.MerchantMapper;
import com.adera.aderapos.identity.mapper.ShopMapper;
import com.adera.aderapos.identity.mapper.AppUserMapper;
import com.adera.aderapos.audit.services.AuditService;
import com.adera.aderapos.audit.entities.enums.AuditAction;
import com.adera.aderapos.audit.entities.enums.AuditEntityType;
import com.adera.aderapos.audit.entities.enums.AuditSeverity;

import java.util.List;
import java.util.UUID;

/**
 * Implementation of the IdentityService interface.
 */
@Service
@RequiredArgsConstructor
public class IdentityServiceImpl implements IdentityService {

    private final MerchantRepository merchantRepository;
    private final ShopRepository shopRepository;
    private final AppUserRepository userRepository;
    private final TelegramBindingRepository telegramBindingRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuditService auditService;

    /**
     * Creates a new merchant.
     * @param merchantDTO the DTO containing merchant details
     * @return the created MerchantDTO
     */
    @Override
    public MerchantDTO createMerchant(MerchantDTO merchantDTO) {
        Merchant merchant = MerchantMapper.toEntity(merchantDTO);
        Merchant saved = merchantRepository.save(merchant);
        auditService.log(
            AuditAction.CREATE,
            AuditEntityType.USER, // No MERCHANT in enum, fallback to USER
            saved.getId(),
            UUID.randomUUID(),
            "null",
            AuditSeverity.LOW,
            "Merchant created"
        );
        return MerchantMapper.toDTO(saved);
    }

    /**
     * Retrieves a merchant by its TIN.
     * @param tin the TIN of the merchant
     * @return the MerchantDTO corresponding to the TIN
     */
    @Override
    public MerchantDTO getMerchantByTinDTO(String tin) {
        Merchant merchant = merchantRepository.findByTin(tin)
                .orElseThrow(() -> new RuntimeException("Merchant not found"));
        return MerchantMapper.toDTO(merchant);
    }

    /**
     * Creates a new shop.
     * @param shopDTO the DTO containing shop details
     * @return the created ShopDTO
     */
    @Override
    public ShopDTO createShop(ShopDTO shopDTO) {
        Merchant merchant = merchantRepository.findById(shopDTO.getMerchantId())
                .orElseThrow(() -> new RuntimeException("Merchant not found"));
        Shop shop = ShopMapper.toEntity(shopDTO, merchant);
        Shop saved = shopRepository.save(shop);
        auditService.log(
            AuditAction.CREATE,
            AuditEntityType.SHOP,
            saved.getId(),
            UUID.randomUUID(),
            "null",
            AuditSeverity.LOW,
            "Shop created"
        );
        return ShopMapper.toDTO(saved);
    }

    /**
     * Retrieves shops by merchant ID.
     * @param merchantId the UUID of the merchant
     * @return a list of ShopDTOs associated with the merchant
     */
    @Override
    public List<ShopDTO> getShopsByMerchantDTO(UUID merchantId) {
        List<Shop> shops = shopRepository.findByMerchantId(merchantId);
        return shops.stream().map(ShopMapper::toDTO).toList();
    }

    /**
     * Creates a new application user.
     * @param userDTO the DTO containing user details
     * @return the created AppUserDTO
     */
    @Override
    public AppUserDTO createUser(AppUserDTO userDTO) {
        Shop shop = shopRepository.findById(userDTO.getShopId())
                .orElseThrow(() -> new RuntimeException("Shop not found"));
        AppUser user = AppUserMapper.toEntity(userDTO, shop);
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        AppUser saved = userRepository.save(user);
        auditService.log(
            AuditAction.CREATE,
            AuditEntityType.USER,
            saved.getId(),
            saved.getId(),
            user.getRole() != null ? user.getRole().name() : null,
            AuditSeverity.LOW,
            "User created"
        );
        return AppUserMapper.toDTO(saved);
    }

    /**
     * Retrieves a user by username.
     * @param username the username of the user
     * @return the AppUserDTO corresponding to the username
     */
    @Override
    public AppUserDTO getUserByUsernameDTO(String username) {
        AppUser user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));
        return AppUserMapper.toDTO(user);
    }

    /**
     * Binds a Telegram user ID to an application user.
     * @param user the application user
     * @param telegramUserId the Telegram user ID to bind
     * @return the created TelegramBinding entity
     */
    @Override
    public TelegramBinding bindTelegram(AppUser user, String telegramUserId) {
        TelegramBinding binding = TelegramBinding.builder()
                .appUser(user)
                .telegramUserId(telegramUserId)
                .verified(false)
                .build();
        return telegramBindingRepository.save(binding);
    }

    @Override
    public AppUserDTO getUser(UUID id) {
        AppUser user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found"));
        return AppUserMapper.toDTO(user);
    }

    @Override
    public List<AppUserDTO> getAllUsers() {
        return userRepository.findAll().stream().map(AppUserMapper::toDTO).toList();
    }

    @Override
    public ShopDTO getShop(UUID id) {
        Shop shop = shopRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Shop not found"));
        return ShopMapper.toDTO(shop);
    }

    @Override
    public List<ShopDTO> getAllShops() {
        return shopRepository.findAll().stream().map(ShopMapper::toDTO).toList();
    }
}
