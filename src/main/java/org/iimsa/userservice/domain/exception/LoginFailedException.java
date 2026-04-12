package org.iimsa.userservice.domain.exception;

import org.iimsa.common.exception.CustomException;
import org.springframework.http.HttpStatus;

public class LoginFailedException extends CustomException {
    public LoginFailedException() {
        super("아이디 또는 비밀번호가 올바르지 않습니다.", HttpStatus.UNAUTHORIZED);
    }
}
