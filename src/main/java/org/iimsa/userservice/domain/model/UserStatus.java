package org.iimsa.userservice.domain.model;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum UserStatus {

    PENDING("보류중"),
    APPROVED("승인됨"),
    REJECTED("거절");

    private final String description;
}
