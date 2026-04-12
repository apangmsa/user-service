package org.iimsa.userservice.application.dto;

import org.iimsa.userservice.infrastructure.keycloak.client.dto.KeycloakTokenResponse;

public record TokenResult(
        String accessToken,
        long expiresIn,
        String refreshToken,
        long refreshExpiresIn,
        String tokenType
) {
    /**
     * 인프라 계층의 데이터를 응용 계층 데이터로 변환하는 정적 팩토리 메서드
     */
    public static TokenResult from(KeycloakTokenResponse response) {
        return new TokenResult(
                response.accessToken(),
                response.expiresIn(),
                response.refreshToken(),
                response.refreshExpiresIn(),
                response.tokenType()
        );
    }
}
