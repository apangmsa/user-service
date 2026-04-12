package org.iimsa.userservice.application.dto.command;

import java.util.UUID;
import org.iimsa.userservice.domain.model.Role;

public record UpdateUserCommand(
        UUID targetUserId,
        Role role,
        UUID hubId,
        UUID companyId,
        String updatedBy
) {
}
