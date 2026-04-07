package org.iimsa.userservice.domain.model;

import static org.iimsa.userservice.domain.model.Role.COMPANY_DELIVERY_MANAGER;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import java.util.UUID;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.iimsa.userservice.domain.exception.InvalidUserException;

@Embeddable
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class DeliveryManager {
    @Column(name = "delivery_hub_id")
    private UUID hubId; // COMPANY_DELIVERY_MANAGER는 필수, HUB_DELIVERY_MANAGER는 선택(확장성)

    @Column(name = "delivery_sequence")
    private Integer sequence;

    private DeliveryManager(UUID hubId, Integer sequence) {
        this.hubId = hubId;
        this.sequence = sequence;
    }

    public static DeliveryManager create(Role role, UUID hubId, Integer sequence) {
        // 업체 배송 담당자는 소속 허브가 필수
        if (role == COMPANY_DELIVERY_MANAGER && hubId == null) {
            throw new InvalidUserException("업체 배송 담당자는 허브 ID가 필수입니다.");
        }
        return new DeliveryManager(hubId, sequence);
    }
}
