package com.adera.aderapos.identity.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import com.adera.aderapos.identity.entities.Merchant;
import java.util.Optional;
import java.util.UUID;

/**
 * Repository interface for Merchant entity.
 */
public interface MerchantRepository extends JpaRepository<Merchant, UUID> {
    /**
     * Find a Merchant by their Tax Identification Number (TIN).
     *
     * @param tin the TIN of the merchant
     * @return an Optional containing the Merchant if found, or empty if not found
     */
    Optional<Merchant> findByTin(String tin);
}
