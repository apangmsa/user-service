package org.iimsa.userservice.domain.model;

import jakarta.persistence.Column;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.util.UUID;
import lombok.Getter;
import org.iimsa.common.domain.BaseEntity;

@Getter
@Entity
@Table(name = "p_user")
public class User extends BaseEntity {
    @Id
    @Column(name = "id")
    private UUID id;

    @Column(length = 50, name = "username")
    private String username;

    @Column(length = 100, name = "email")
    private String email;

    @Column(length = 100, name = "slack_id")
    private String slackId;

    @Column(length = 20, nullable = false, name = "role")
    @Enumerated(EnumType.STRING)
    private UserRole userRole;

    @Column(name = "status")
    @Enumerated(EnumType.STRING)
    private UserStatus userStatus;

    @Embedded
    private DeliveryManager deliveryManager; // 직원 소속 : 허브/업체/본사

}
