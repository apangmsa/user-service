package org.iimsa.userservice.domain.exception;

public class InvalidUserException extends RuntimeException {
    public InvalidUserException() {
        this("유효하지 않은 사용자입니다.");
    }

    public InvalidUserException(String message) {
        super(message);
    }
}
