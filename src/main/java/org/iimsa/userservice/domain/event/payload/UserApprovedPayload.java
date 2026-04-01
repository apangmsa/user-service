package org.iimsa.userservice.domain.event.payload;

import java.util.UUID;
import org.iimsa.userservice.domain.model.User;

public record UserApprovedPayload(
        UUID userId,
        String name,
        String role,
        int deliveryRotationOrder,
        String email,
        String slackId,
        String approvedBy
) {
    public static UserApprovedPayload from(User user) {
        return new UserApprovedPayload(
                user.getId(),
                user.getUsername(),
                user.getUserRole().toString(),
                user.getDeliveryManager().getDeliverySequence(),
                user.getEmail(),
                user.getSlackId(),
                user.getModifiedBy()
        );
    }
/*
// TODO: JPA 의존하지 않게
public static UserApprovedPayload from(User user, String approvedBy) {
    return new UserApprovedPayload(
            user.getId(),
            user.getUsername(),
            user.getUserRole().toString(),
            user.getDeliveryManager().getDeliverySequence(),
            user.getEmail(),
            user.getSlackId(),
            approvedBy
    );
}*/

}