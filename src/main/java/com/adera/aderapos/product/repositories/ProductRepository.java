package com.adera.aderapos.product.repositories;

import com.adera.aderapos.product.entities.Product;
import com.adera.aderapos.identity.entities.Shop;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Repository interface for Product entity.
 */
public interface ProductRepository extends JpaRepository<Product, UUID> {

    List<Product> findByShop(Shop shop);

    Optional<Product> findByShopAndSku(Shop shop, String sku);
}
