package org.iimsa.userservice.domain.event.payload;

import java.util.UUID;

public record UserApprovedPayload(
        UUID userId,
        String name,
        String role,
        int deliveryRotationOrder,
        String email,
        String slackId,
        String approvedBy
) {

    public static UserApprovedPayload of(
            UUID userId,
            String name,
            String role,
            int deliveryRotationOrder,
            String email,
            String slackId,
            String approvedBy
    ) {
        return new UserApprovedPayload(userId, name, role, deliveryRotationOrder, email, slackId, approvedBy);
    }

}
