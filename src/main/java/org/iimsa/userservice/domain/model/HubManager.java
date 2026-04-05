package org.iimsa.userservice.domain.model;

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
public class HubManager {
    @Column(name = "manage_hub_id")
    private UUID hubId;

    private HubManager(UUID hubId) {
        this.hubId = hubId;
    }

    public static HubManager create(UUID hubId) {
        if (hubId == null) {
            throw new InvalidUserException("허브 ID는 필수입니다.");
        }
        return new HubManager(hubId);
    }
}
