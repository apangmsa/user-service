package org.iimsa.userservice.domain.model;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum UserRole {
    MASTER("마스터 관리자"),

    HUB_MANAGER("허브 관리자"),
    HUB_DELIVERY_MANAGER("허브 배송 담당자"),

    COMPANY_MANAGER("업체 담당자"),
    COMPANY_DELIVERY_MANAGER("업체 배송 담당자");

    private final String description;

    public String toRole() {
        return "ROLE_" + this.name();
    }
}