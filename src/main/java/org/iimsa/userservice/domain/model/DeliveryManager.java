package org.iimsa.userservice.domain.model;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import java.util.UUID;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Embeddable
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class DeliveryManager {
    @Column(name = "hub_id")
    private UUID hubId;
    @Column(name = "delivery_sequence")
    private int deliverySequence;

    public DeliveryManager(UserRole role,
                           UUID hubId,
                           int deliverySequence) {

        if (!isDeliveryManager(role)) {
            // TODO: Exception
            throw new IllegalArgumentException("배송 담당자만 DeliveryManager를 가질 수 있습니다.");
        }

        if (role == UserRole.HUB_DELIVERY_MANAGER) {
            if (hubId == null) {
                // TODO: Exception
                throw new IllegalArgumentException("허브 배송 담당자는 hubId가 필수입니다.");
            }
            this.hubId = hubId;
        }

        this.deliverySequence = deliverySequence;

    }

    private boolean isDeliveryManager(UserRole role) {
        return role == UserRole.HUB_DELIVERY_MANAGER
                || role == UserRole.COMPANY_DELIVERY_MANAGER;
    }

}