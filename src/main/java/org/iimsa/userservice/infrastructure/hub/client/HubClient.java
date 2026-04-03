package org.iimsa.userservice.infrastructure.hub.client;

import java.util.UUID;
import org.iimsa.userservice.infrastructure.hub.client.dto.HubResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(
        name = "hub-service",
        fallbackFactory = HubClientFallbackFactory.class
)
public interface HubClient {
    @GetMapping("/api/v1/hubs/{hubId}")
    HubResponse getHub(@PathVariable("hubId") UUID hubId);
}
