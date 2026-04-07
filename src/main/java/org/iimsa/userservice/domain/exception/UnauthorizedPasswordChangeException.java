package org.iimsa.userservice.domain.exception;

public class UnauthorizedPasswordChangeException extends RuntimeException {
    public UnauthorizedPasswordChangeException() {
        this("비밀번호 변경 권한이 없습니다.");
    }

    public UnauthorizedPasswordChangeException(String message) {
        super(message);
    }
}
