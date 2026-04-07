package org.iimsa.userservice.domain.event.payload;

import java.time.LocalDateTime;
import java.util.UUID;
import org.iimsa.userservice.domain.model.User;

public record UserDeletedPayload(
        UUID userId,
        String name,
        String type,
        Integer deliverySequence, // 퇴사시 배송 순번(허브 배송 담당자, 다른 담당자라면 null),
        LocalDateTime deletedAt,
        String deletedBy
) {
    public static UserDeletedPayload from(User user) {
        Integer deliverySequence = user.getDeliveryManager() == null
                ? null
                : user.getDeliveryManager().getSequence();
        return new UserDeletedPayload(
                user.getId(),
                user.getUsername(),
                user.getRole().toString(),
                deliverySequence,
                user.getDeletedAt(),
                user.getDeletedBy()
        );
    }
}
