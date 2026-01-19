package com.adera.aderapos.identity.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import com.adera.aderapos.identity.entities.TelegramBinding;
import java.util.Optional;
import java.util.UUID;

/**
 * Repository interface for TelegramBinding entity.
 */
public interface TelegramBindingRepository extends JpaRepository<TelegramBinding, UUID> {
    /**
     * Find a TelegramBinding by the Telegram User ID.
     *
     * @param telegramUserId the Telegram User ID
     * @return an Optional containing the TelegramBinding if found, or empty if not found
     */
    Optional<TelegramBinding> findByTelegramUserId(String telegramUserId);
}
