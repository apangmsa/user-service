package org.iimsa.userservice.domain.exception;

import org.iimsa.common.exception.CustomException;
import org.springframework.http.HttpStatus;

public class SessionExpiredException extends CustomException {
    public SessionExpiredException() {
        super("세션이 만료되었습니다. 다시 시도해주세요.", HttpStatus.UNAUTHORIZED);
    }
}
