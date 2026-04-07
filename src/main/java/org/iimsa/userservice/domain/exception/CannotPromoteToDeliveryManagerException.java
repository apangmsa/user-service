package org.iimsa.userservice.domain.exception;

public class CannotPromoteToDeliveryManagerException extends RuntimeException {
    public CannotPromoteToDeliveryManagerException() {
        this("배송담당자를 지정할 수 없습니다.");
    }

    public CannotPromoteToDeliveryManagerException(String message) {
        super(message);
    }
}
