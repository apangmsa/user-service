package org.iimsa.userservice.domain.repository;

import java.util.Optional;
import java.util.UUID;
import org.iimsa.userservice.domain.model.User;

public interface UserRepository {
    User save(User user);

    Optional<User> findById(UUID id);
}
