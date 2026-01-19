package com.adera.aderapos.sales.repositories;

import com.adera.aderapos.sales.entities.Sale;
import com.adera.aderapos.identity.entities.Shop;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface SaleRepository extends JpaRepository<Sale, UUID> {

    List<Sale> findByShop(Shop shop);
    List<Sale> findByUserId(UUID userId);

}
