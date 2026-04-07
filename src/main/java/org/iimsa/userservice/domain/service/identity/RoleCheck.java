package org.iimsa.userservice.domain.service.identity;

import java.util.List;
import java.util.UUID;
import org.iimsa.userservice.domain.model.Role;

public interface RoleCheck {
    boolean hasRole(Role role);

    boolean hasRole(List<Role> types);

    boolean isMine(UUID id);
}
