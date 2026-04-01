package org.iimsa.userservice.domain.exception;

import jakarta.ws.rs.BadRequestException;

public class InvalidEmailException extends BadRequestException {
    public InvalidEmailException() {
        this("이메일 형식이 올바르지 않습니다.");
    }

    public InvalidEmailException(String message) {
        super(message);
    }
}
