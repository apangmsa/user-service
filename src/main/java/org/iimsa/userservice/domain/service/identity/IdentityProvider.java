package org.iimsa.userservice.domain.service.identity;

import java.util.UUID;

/**
 * 유저 서비스 외의 인증 서비스를 통해 회원을 등록하고 변경합니다.
 */
public interface IdentityProvider {

    // 원격 회원 등록
    UUID register(String email, String password);

    // 원격 회원 등록 취소
    void withdraw(UUID userId);

    // 비밀번호 변경
    void changePassword(UUID userId, String newPassword);
}
