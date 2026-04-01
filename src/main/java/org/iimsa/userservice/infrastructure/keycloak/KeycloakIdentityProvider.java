package org.iimsa.userservice.infrastructure.keycloak;

import jakarta.ws.rs.NotFoundException;
import jakarta.ws.rs.core.Response;
import java.util.Collections;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.iimsa.common.exception.ConflictException;
import org.iimsa.userservice.domain.exception.IdentityProviderException;
import org.iimsa.userservice.domain.exception.UserNotFoundException;
import org.iimsa.userservice.domain.service.identity.IdentityProvider;
import org.iimsa.userservice.infrastructure.keycloak.config.KeycloakProperties;
import org.keycloak.admin.client.Keycloak;
import org.keycloak.admin.client.resource.UsersResource;
import org.keycloak.representations.idm.CredentialRepresentation;
import org.keycloak.representations.idm.UserRepresentation;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class KeycloakIdentityProvider implements IdentityProvider {

    private final Keycloak keycloak;
    private final KeycloakProperties properties;

    @Override
    public UUID register(String email, String password) {
        // UserRepresentation 설정
        UserRepresentation user = new UserRepresentation();
        user.setEnabled(true);
        user.setUsername(email.toLowerCase());
        user.setEmail(email);
        user.setEmailVerified(true); // 이메일 인증이 필요한 경우 false로 설정하고, 인증 완료 시 true로 변경한다.
        user.setRequiredActions(Collections.emptyList()); // 강제 액션을 모두 제거

        // 비밀번호 설정
        CredentialRepresentation credential = getCredential(password);
        user.setCredentials(Collections.singletonList(credential));

        // Keycloak에 사용자 생성 요청
        UsersResource usersResource = keycloak.realm(properties.realm()).users();
        try (Response response = usersResource.create(user)) {
            if (response.getStatus() == HttpStatus.CONFLICT.value()) {
                throw new ConflictException("이미 등록된 이메일 주소입니다.");
            }

            if (response.getStatus() != HttpStatus.CREATED.value()) {
                log.error("Keycloak 회원 등록 실패: 상태코드 {}", response.getStatus());

                throw new IdentityProviderException("인증 서버 등록에 실패하였습니다.");
            }

            // 생성된 사용자의 UUID 추출 (Location 헤더에 포함됨)
            if (response.getLocation() == null) {
                log.error("Keycloak 응답에 Location 헤더가 누락되었습니다.");
                throw new IdentityProviderException("인증 서버로부터 식별자를 받아오지 못했습니다.");
            }

            String userId = response.getLocation().getPath().replaceAll(".*/([^/]+)$", "$1");
            log.info("Keycloak 회원 등록 성공: {}", userId);

            return UUID.fromString(userId);
        }
    }

    @Override
    public void withdraw(UUID userId) {
        try {
            keycloak.realm(properties.realm()).users().get(userId.toString()).remove();
        } catch (Exception e) {
            // 보상 트랜잭션 실패 시 수동 조치할 수 있도록 로그를 남긴다.
            log.error("Keycloak 회원 삭제 실패 (ID: {}): {}", userId, e.getMessage());
        }
    }

    @Override
    public void changePassword(UUID userId, String newPassword) {
        UsersResource usersResource = keycloak.realm(properties.realm()).users();

        CredentialRepresentation credential = getCredential(newPassword);

        try {
            // 비밀번호 변경 요청
            usersResource.get(userId.toString()).resetPassword(credential);
            log.info("Keycloak 비밀번호 변경 성공: {}", userId);

        } catch (NotFoundException e) {
            log.error("Keycloak 유저를 찾을 수 없음 (ID: {}): {}", userId, e.getMessage());
            throw new UserNotFoundException("인증 서버에서 해당 사용자를 찾을 수 없습니다.");

        } catch (Exception e) {
            log.error("Keycloak 비밀번호 변경 실패 (ID: {}): {}", userId, e.getMessage());
            throw new IdentityProviderException("인증 서버의 비밀번호 변경 중 오류가 발생했습니다.");
        }
    }

    // Keycloak API 포맷을 맞추기 위한 변환 헬퍼 (credential = 비밀번호 방식)
    private CredentialRepresentation getCredential(String password) {
        CredentialRepresentation credential = new CredentialRepresentation();
        credential.setType(CredentialRepresentation.PASSWORD);
        credential.setValue(password);
        credential.setTemporary(false);

        return credential;
    }
}
