package org.iimsa.userservice.domain.model;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import java.util.UUID;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.iimsa.userservice.domain.exception.InvalidUserException;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Embeddable
public class DeliveryManager {

    @Column(name = "hub_id")
    private UUID hubId; // 각 허브 별로 10명의 업체 배송 담당자가 있습니다. 허브 배송 담당자는 물류 시스템 전체에서 총 10명이 존재합니다.

    @Column(name = "delivery_sequence")
    private int sequence; // 순차 배정을 위한 번호

    // 생성자에서 비즈니스 로직에 따른 필드 강제화
    private DeliveryManager(UUID hubId, int sequence) {
        this.hubId = hubId;
        this.sequence = sequence;
    }

    public static DeliveryManager create(UserRole role, UUID hubId, int sequence) {
        if (role == UserRole.COMPANY_DELIVERY_MANAGER) {
            if (hubId == null) {
                throw new InvalidUserException("업체 배송 담당자는 허브 ID가 필수입니다.");
            }
            return new DeliveryManager(hubId, sequence);
        }

        if (role == UserRole.HUB_DELIVERY_MANAGER) {
            // 허브 담당자는 hubId를 null로 강제
            return new DeliveryManager(null, sequence);
        }

        throw new InvalidUserException("배송 담당자가 아닌 역할입니다.");
    }
}
