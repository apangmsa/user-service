package org.iimsa.userservice.presentation.dto;

import java.util.UUID;
import org.iimsa.userservice.domain.model.Role;

public record UpdateRoleRequest(
        // MASTER만 수정 가능
        Role role,
        UUID hubId,
        UUID companyId
) {
}
