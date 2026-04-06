package org.iimsa.userservice.domain.event.payload;

import java.time.LocalDateTime;
import java.util.UUID;
import org.iimsa.userservice.domain.model.Role;
import org.iimsa.userservice.domain.model.User;

public record UserDeletedPayload(
        UUID userId,
        String username,
        String email,
        Role role,
        Integer deliverySequence, // 배송기사라면, 재배정 시 순번 참고용
        LocalDateTime deletedAt,
        String deletedBy
) {
    public static UserDeletedPayload from(User user) {
        return new UserDeletedPayload(
                user.getId(),
                user.getUsername(),
                user.getEmail(),
                user.getRole(),
                user.getDeliveryManager() == null ? null : user.getDeliveryManager().getSequence(),
                user.getDeletedAt(),
                user.getDeletedBy()
        );
    }
}
