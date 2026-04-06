package org.iimsa.userservice.domain.event.payload;

import java.util.UUID;
import org.iimsa.userservice.domain.model.Role;
import org.iimsa.userservice.domain.model.User;

public record UserApprovedPayload(
        UUID userId,
        String name,
        String email,
        String slackId,
        Role role,
        UUID hubId,
        UUID companyId,
        Integer deliverySequence
) {
    public static UserApprovedPayload from(User user) {
        Role role = user.getRole();

        UUID hubId = null;
        UUID companyId = null;
        Integer deliverySequence = null;

        if (user.getHubManager() != null) { // 허브 관리자
            hubId = user.getHubManager().getHubId();
        } else if (user.getCompanyManager() != null) { // 업체 담당자
            companyId = user.getCompanyManager().getCompanyId();
        } else if (user.getDeliveryManager() != null) { // 배송기사
            deliverySequence = user.getDeliveryManager().getSequence();
            if (role == Role.COMPANY_DELIVERY_MANAGER) {
                hubId = user.getDeliveryManager().getHubId();
            }
        }

        return new UserApprovedPayload(
                user.getId(),
                user.getUsername(),
                user.getEmail(),
                user.getSlackId(),
                user.getRole() == null ? null : user.getRole(),
                hubId,
                companyId,
                deliverySequence
        );
    }
}
