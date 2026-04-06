package org.iimsa.userservice.domain.event.payload;

import java.time.LocalDateTime;
import java.util.UUID;
import org.iimsa.userservice.domain.model.Role;
import org.iimsa.userservice.domain.model.User;

public record UserDeletedPayload(
        UUID userId,
        String name,
        Integer deliverySequence, // 퇴사시 배송 순번(허브 배송 담당자, 다른 담당자라면 null),
        Role role,
        LocalDateTime deletedAt,
        String deletedBy
) {
    public static UserDeletedPayload from(User user) {
        Integer deliverySequence = user.getDeliveryManager() == null
                ? null
                : user.getDeliveryManager().getSequence();
        Role role = user.getRole() == null ? user.getRequestedRole() : user.getRole();
        return new UserDeletedPayload(
                user.getId(),
                user.getUsername(),
                deliverySequence,
                role,
                user.getDeletedAt(),
                user.getDeletedBy()
        );
    }
}
