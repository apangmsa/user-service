package org.iimsa.userservice.infrastructure.keycloak;

import java.security.MessageDigest;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.iimsa.common.exception.CustomException;
import org.iimsa.common.exception.UnAuthorizedException;
import org.iimsa.userservice.application.dto.TokenResult;
import org.iimsa.userservice.application.dto.command.LoginCommand;
import org.iimsa.userservice.application.dto.command.RefreshTokenCommand;
import org.iimsa.userservice.application.service.AuthService;
import org.iimsa.userservice.domain.exception.LoginFailedException;
import org.iimsa.userservice.domain.exception.SessionExpiredException;
import org.iimsa.userservice.infrastructure.keycloak.client.KeycloakClient;
import org.iimsa.userservice.infrastructure.keycloak.client.dto.KeycloakTokenResponse;
import org.iimsa.userservice.infrastructure.keycloak.config.KeycloakProperties;
import org.springframework.http.HttpStatus;
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
                    "username", loginCommand.email(),
                    "password", loginCommand.password(),
                    "scope", "openid"
            );

            log.info("Keycloak 인증 시도 (Email: {})", loginCommand.email());
            log.info("login attempt (email: {}, hash: {})",
                    maskEmail(loginCommand.email()),
                    hashEmail(loginCommand.email())
            );
            KeycloakTokenResponse response = keycloakClient.getToken(params);
            return TokenResult.from(response);
        } catch (feign.FeignException.Unauthorized e) { // 401
            throw new LoginFailedException();
        } catch (feign.FeignException e) {
            // 그 외 Keycloak 관련 통신 에러 처리 (필요 시)
            log.error("Keycloak auth error: {}", e.getMessage());
            throw new CustomException("인증 서비스 통신 중 오류가 발생했습니다.", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @Override
    public TokenResult refreshToken(RefreshTokenCommand refreshTokenCommand) {
        try {
            Map<String, String> params = Map.<String, String>of(
                    "grant_type", "refresh_token",
                    "client_id", properties.clientId(),
                    "client_secret", properties.clientSecret(),
                    "refresh_token", refreshTokenCommand.refreshToken()
            );

            log.info("Keycloak 토큰 갱신 시도");
            KeycloakTokenResponse response = keycloakClient.getToken(params);
            return TokenResult.from(response);
        } catch (UnAuthorizedException e) { // TODO: 500 처리됨
            log.error("토큰 갱신 실패: {}", e.getMessage(), e);
            throw new SessionExpiredException();
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

            keycloakClient.logout(params);
            log.info("Keycloak 로그아웃 성공");
        } catch (UnAuthorizedException e) {
            log.info("이미 로그아웃 상태 또는 토큰 무효");
        }
    }

    private String maskEmail(String email) {
        if (email == null || email.isEmpty()) {
            return "";
        }

        int atIndex = email.indexOf("@");
        if (atIndex <= 2) {
            return "***" + email.substring(atIndex);
        }

        String prefix = email.substring(0, 2);
        String domain = email.substring(atIndex);

        return prefix + "****" + domain;
    }


    private String hashEmail(String email) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            byte[] digest = md.digest(email.getBytes());

            StringBuilder hex = new StringBuilder();
            for (byte b : digest) {
                hex.append(String.format("%02x", b));
            }
            return hex.substring(0, 8); // 앞 8자리만 사용
        } catch (Exception e) {
            return "unknown";
        }
    }
}
