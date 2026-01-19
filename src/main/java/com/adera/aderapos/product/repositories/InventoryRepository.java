package com.adera.aderapos.product.repositories;

import com.adera.aderapos.product.entities.Inventory;
import com.adera.aderapos.product.entities.Product;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

/**
 * Repository interface for Inventory entity.
 */
public interface InventoryRepository extends JpaRepository<Inventory, UUID> {

    Optional<Inventory> findByProduct(Product product);

}
