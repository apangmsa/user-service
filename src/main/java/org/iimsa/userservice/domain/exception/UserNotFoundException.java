package org.iimsa.userservice.domain.exception;

import org.iimsa.common.exception.NotFoundException;

/**
 * 이상적인 구조: domain/exception → 순수 RuntimeException (HTTP 모름)
 * <p>
 * presentation     → GlobalExceptionHandler에서 HTTP 변환
 * <p>
 * <p>
 * 현재 팀 구조: domain/exception → CustomException(HttpStatus 포함)
 * <p>
 * GlobalExceptionAdvice → CustomException 계층 전체를 한번에 처리
 *
 */
public class UserNotFoundException extends NotFoundException {
    public UserNotFoundException() {
        this("사용자를 찾을 수 없습니다.");
    }

    public UserNotFoundException(String message) {
        super(message);
    }

}
