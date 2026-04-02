package org.iimsa.userservice.domain.exception;

public class InvalidPasswordException extends RuntimeException {
    public InvalidPasswordException() {
        this("비밀번호 형식이 올바르지 않습니다.");
    }

    public InvalidPasswordException(String message) {
        super(message);
    }
}
