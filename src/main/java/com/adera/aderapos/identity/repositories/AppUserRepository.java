package com.adera.aderapos.identity.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import com.adera.aderapos.identity.entities.AppUser;
import java.util.Optional;
import java.util.UUID;

/**
 * Repository interface for AppUser entity.
 */
public interface AppUserRepository extends JpaRepository<AppUser, UUID> {
    /**
     * Find an AppUser by their username.
     *
     * @param username the username of the user
     * @return an Optional containing the AppUser if found, or empty if not found
     */
    Optional<AppUser> findByUsername(String username);

    /**
     * Find an AppUser by their Telegram ID.
     *
     * @param telegramId the Telegram ID of the user
     * @return an Optional containing the AppUser if found, or empty if not found
     */
    Optional<AppUser> findByTelegramId(String telegramId);
}
