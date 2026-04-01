package org.iimsa.userservice.domain.exception;

import java.util.UUID;
import org.iimsa.common.exception.NotFoundException;

public class HubNotFoundException extends NotFoundException {
    public HubNotFoundException(UUID hubId) {
        super("등록되지 않은 허브 정보입니다. HUB ID: " + hubId.toString());
    }
}
