package org.iimsa.userservice.infrastructure.keycloak.client;

import java.util.Map;
import org.iimsa.userservice.infrastructure.keycloak.client.dto.KeycloakTokenResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;

@FeignClient(
        name = "keycloak-client",
        url = "${keycloak.server-url}",
        fallbackFactory = KeycloakClientFallbackFactory.class
)
public interface KeycloakClient {
    @PostMapping(path = "/realms/${keycloak.realm}/protocol/openid-connect/token", consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE)
    KeycloakTokenResponse getToken(Map<String, String> params);

    @PostMapping(path = "/realms/${keycloak.realm}/protocol/openid-connect/logout",
            consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE)
    void logout(Map<String, String> params);
}
