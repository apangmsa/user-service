package org.iimsa.userservice.application.dto.command;

import java.util.UUID;
import org.iimsa.userservice.domain.model.Role;

public record ApproveUserCommand(
        UUID targetUserId,
        Role requestedRole,   // master가 부여하려는 역할 (검증용)
        UUID hubId,           // HUB_MANAGER, HUB_DELIVERY_MANAGER, COMPANY_DELIVERY_MANAGER
        UUID companyId,       // COMPANY_MANAGER
        String approvedBy     // 처리한 master 사용자명 (감사 로그)
) {
}
