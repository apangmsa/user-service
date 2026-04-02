package org.iimsa.userservice.domain.model;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import java.util.UUID;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Builder(access = AccessLevel.PRIVATE)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Embeddable
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class DeliveryManager {
    @Column(name = "hub_id")
    private UUID hubId;
    @Column(name = "delivery_sequence")
    private int deliverySequence;

    public static DeliveryManager create(UserRole role, UUID hubId) {
        if (!isDeliveryManager(role)) {
            // TODO: Exception
            throw new IllegalArgumentException("배송 담당자만 DeliveryManager를 가질 수 있습니다.");
        }

        if (role == UserRole.HUB_DELIVERY_MANAGER) {
            if (hubId == null) {
                // TODO: Exception
                throw new IllegalArgumentException("허브 배송 담당자는 hubId가 필수입니다.");
            }
        }

        return DeliveryManager.builder()
                .hubId(hubId)
                .build();
    }

    private static boolean isDeliveryManager(UserRole role) {
        return role == UserRole.HUB_DELIVERY_MANAGER
                || role == UserRole.COMPANY_DELIVERY_MANAGER;
    }

    public void assignSequence(int sequence) {
        this.deliverySequence = sequence;
    }
}