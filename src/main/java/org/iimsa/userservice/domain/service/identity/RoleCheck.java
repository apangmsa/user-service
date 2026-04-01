package org.iimsa.userservice.domain.service.identity;

import java.util.List;
import java.util.UUID;
import org.iimsa.userservice.domain.model.UserRole;

public interface RoleCheck {
    boolean hasRole(UserRole role);

    boolean hasRole(List<UserRole> types);

    boolean isMine(UUID id);
}
