package com.adera.aderapos.identity.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import com.adera.aderapos.identity.entities.Shop;
import java.util.List;
import java.util.UUID;

/**
 * Repository interface for Shop entity.
 */
public interface ShopRepository extends JpaRepository<Shop, UUID> {
    /**
     * Find all Shops associated with a specific Merchant ID.
     *
     * @param merchantId the UUID of the merchant
     * @return a list of Shops associated with the given merchant ID
     */
    List<Shop> findByMerchantId(UUID merchantId);
}
