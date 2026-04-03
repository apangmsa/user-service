package org.iimsa.userservice.domain.query;

import java.util.Optional;
import java.util.UUID;
import org.iimsa.userservice.domain.model.User;
import org.iimsa.userservice.domain.model.UserRole;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface UserQueryRepository {
    Optional<User> findById(UUID id);

    Page<User> findAllByHubId(UUID hubId, UserQueryDto.Search search, Pageable pageable);

    Page<User> findAllByCompanyId(UUID companyId, UserQueryDto.Search search, Pageable pageable);

    Page<User> findAllByRole(UserRole role, UserQueryDto.Search search, Pageable pageable);

    Page<User> findAll(UserQueryDto.Search search, Pageable pageable);
}
