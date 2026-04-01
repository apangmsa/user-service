package org.iimsa.userservice.domain.exception;

import org.iimsa.common.exception.CustomException;
import org.springframework.http.HttpStatus;

public class IdentityProviderException extends CustomException {
    public IdentityProviderException(String message) {
        super(message, HttpStatus.BAD_GATEWAY); // 외부통신 오류는 BAD_GATEWAY(502)
    }
}
