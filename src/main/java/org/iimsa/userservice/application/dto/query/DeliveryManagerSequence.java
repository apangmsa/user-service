package org.iimsa.userservice.application.dto.query;

import java.util.UUID;

public record DeliveryManagerSequence(
        UUID userId,
        String name,
        String slackId,
        Integer nextSequence
) {
}
