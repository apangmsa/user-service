package org.iimsa.userservice.infrastructure.keycloak;

import java.util.Map;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.iimsa.common.exception.InternalServerException;
import org.iimsa.common.exception.UnAuthorizedException;
import org.iimsa.userservice.application.dto.TokenResult;
import org.iimsa.userservice.application.dto.command.LoginCommand;
import org.iimsa.userservice.application.service.AuthService;
import org.iimsa.userservice.infrastructure.keycloak.client.KeycloakClient;
import org.iimsa.userservice.infrastructure.keycloak.client.dto.KeycloakTokenResponse;
import org.iimsa.userservice.infrastructure.keycloak.config.KeycloakProperties;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class KeycloakAuthService implements AuthService {

    private final KeycloakClient keycloakClient;
    private final KeycloakProperties properties;

    @Override
    public TokenResult getToken(LoginCommand loginCommand) {
        try {
            Map<String, String> params = Map.of(
                    "grant_type", "password",
                    "client_id", properties.clientId(),
                    "client_secret", properties.clientSecret(),
                    "username", loginCommand.loginId(),
                    "password", loginCommand.password(),
                    "scope", "openid"
            );

            log.info("Keycloak 인증 시도 (Email: {})", loginCommand.loginId());
            KeycloakTokenResponse response = keycloakClient.getToken(params);
            return TokenResult.from(response);
        } catch (Exception e) {
            log.error("인증 처리 중 오류 발생 (Email: {}): {}", loginCommand.loginId(), e.getMessage());
            throw new UnAuthorizedException("이메일 또는 비밀번호가 올바르지 않습니다.");
        }
    }

    @Override
    public TokenResult refreshToken(String refreshToken) {
        try {
            Map<String, String> params = Map.of(
                    "grant_type", "refresh_token",
                    "client_id", properties.clientId(),
                    "client_secret", properties.clientSecret(),
                    "refresh_token", refreshToken
            );

            log.info("Keycloak 토큰 갱신 시도");
            KeycloakTokenResponse response = keycloakClient.getToken(params);
            return TokenResult.from(response);
        } catch (Exception e) {
            log.error("토큰 갱신 실패: {}", e.getMessage(), e);
            throw new UnAuthorizedException("세션이 만료되었습니다. 다시 로그인해주세요.");
        }
    }

    @Override
    public void logout(String refreshToken) {
        try {
            Map<String, String> params = Map.of(
                    "client_id", properties.clientId(),
                    "client_secret", properties.clientSecret(),
                    "refresh_token", refreshToken
            );

            log.info("Keycloak 로그아웃 시도 (세션 만료)");
            keycloakClient.logout(params);
        } catch (Exception e) {
            log.error("로그아웃 처리 중 오류 발생: {}", e.getMessage(), e);
            throw new InternalServerException("로그아웃 처리 중 오류가 발생했습니다.");
        }
    }
}
