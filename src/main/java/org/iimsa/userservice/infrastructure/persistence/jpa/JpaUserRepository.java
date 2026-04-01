package org.iimsa.userservice.infrastructure.persistence.jpa;

import java.util.Optional;
import java.util.UUID;
import org.iimsa.userservice.domain.model.User;
import org.iimsa.userservice.domain.repository.UserRepository;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface JpaUserRepository extends UserRepository, JpaRepository<User, UUID> {

    @Query("""
                SELECT COALESCE(MAX(u.deliveryManager.deliverySequence), 0)
                FROM User u
                WHERE u.userRole = 'HUB_DELIVERY_MANAGER'
            """)
    int findMaxDeliverySequence();

    Optional<User> findById(UUID userId);
}
