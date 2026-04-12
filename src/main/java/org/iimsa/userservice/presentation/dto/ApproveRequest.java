package org.iimsa.userservice.presentation.dto;

import jakarta.validation.constraints.NotNull;
import java.util.UUID;
import org.iimsa.userservice.domain.model.Role;

public record ApproveRequest(
        @NotNull
        Role requestedRole,
        UUID hubId,       // nullable - 역할에 따라
        UUID companyId    // nullable - 역할에 따라
) {
}
