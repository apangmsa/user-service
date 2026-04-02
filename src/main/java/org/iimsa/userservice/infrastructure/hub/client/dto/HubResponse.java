package org.iimsa.userservice.infrastructure.hub.client.dto;

import java.util.UUID;

public record HubResponse(
        UUID id,
        String name,
        String address
) {
}
