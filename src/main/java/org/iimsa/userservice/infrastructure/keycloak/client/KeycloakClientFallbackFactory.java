package org.iimsa.userservice.infrastructure.keycloak.client;


import feign.FeignException;
import java.util.Map;
import lombok.extern.slf4j.Slf4j;
import org.iimsa.common.exception.CustomException;
import org.iimsa.common.exception.NotFoundException;
import org.iimsa.common.exception.UnAuthorizedException;
import org.iimsa.userservice.infrastructure.keycloak.client.dto.KeycloakTokenResponse;
import org.springframework.cloud.openfeign.FallbackFactory;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class KeycloakClientFallbackFactory implements FallbackFactory<KeycloakClient> {

    @Override
    public KeycloakClient create(Throwable e) {
        log.error("Keycloak 인증 서버 통신 중 오류 발생: {}", e.getMessage());

        // 메서드가 여러 개이므로 익명 객체로 반환하여 각각 정의합니다.
        return new KeycloakClient() {

            @Override
            public KeycloakTokenResponse getToken(Map<String, String> params) {
                handleException(e);
                return null;
            }

            @Override
            public void logout(Map<String, String> params) {
                handleException(e);
            }

            // 공통 예외 처리 로직 추출
            private void handleException(Throwable e) {
                if (e instanceof FeignException.Unauthorized) {
                    log.warn("Keycloak 인증 실패 (401): 잘못된 클라이언트 정보 또는 사용자 자격 증명");
                    throw new UnAuthorizedException("인증 서버 접근 권한이 없거나 자격 증명이 틀립니다.");
                }

                if (e instanceof FeignException.NotFound) {
                    log.warn("Keycloak 엔드포인트를 찾을 수 없음 (404)");
                    throw new NotFoundException("인증 서버의 주소가 올바르지 않습니다.");
                }

                log.error("인증 서버 시스템 장애 발생 (Cause: {})", e.getClass().getSimpleName());
                throw new CustomException("현재 인증 서버를 사용할 수 없습니다. 잠시 후 다시 시도해주세요.", HttpStatus.BAD_GATEWAY);
            }
        };
    }
}
