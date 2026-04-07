package org.iimsa.userservice.infrastructure.persistence.jpa;

import java.util.UUID;
import org.iimsa.userservice.domain.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface JpaUserRepository extends org.iimsa.userservice.domain.repository.UserRepository,
        JpaRepository<User, UUID> {
}
