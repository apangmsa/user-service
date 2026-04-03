package org.iimsa.userservice.domain.model;

import static org.iimsa.userservice.domain.model.UserRole.MASTER;

import jakarta.persistence.Column;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.util.UUID;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.iimsa.common.domain.BaseEntity;
import org.iimsa.userservice.domain.exception.CannotPromoteToDeliveryManagerException;
import org.iimsa.userservice.domain.exception.InvalidEmailException;
import org.iimsa.userservice.domain.exception.InvalidPasswordException;
import org.iimsa.userservice.domain.exception.UnauthorizedPasswordChangeException;
import org.iimsa.userservice.domain.service.identity.RoleCheck;
import org.springframework.util.StringUtils;

@Builder(access = AccessLevel.PRIVATE)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@Entity
@Table(name = "p_user")
public class User extends BaseEntity {

    private static final String PASSWORD_REGEX =
            "^(?=.*[a-z])(?=.*[0-9])(?=.*[^a-zA-Z0-9ㄱ-ㅎㅏ-ㅣ가-힣\\s])[a-zA-Z0-9ㄱ-ㅎㅏ-ㅣ가-힣\\s\\S]{8,20}$";
    private static final String EMAIL_REGEX = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$";
    private static final String LOGIN_ID_REGEX = "^[a-z0-9]{4,10}$";

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


    // User 엔티티 내부
    public static User create(UUID id, String username, String email, String slackId, UserRole role, UUID hubId) {
        validateEmail(email);

        UserBuilder builder = User.builder()
                .id(id)
                .username(username)
                .email(email)
                .slackId(slackId)
                .userRole(role)
                .deliveryManager(null); // 승인 단계에서 넣어줌

        return builder.build();
    }

    // ===================유효성 검사
    private static void validatePassword(String password) {
        if (!StringUtils.hasText(password) || !password.matches(PASSWORD_REGEX)) {
            throw new InvalidPasswordException("비밀번호는 영문, 숫자, 특수문자를 포함하여 8~20자여야 합니다.");
        }
    }

    // 배송기사 순번 할당

    private static void validateEmail(String email) {
        if (!StringUtils.hasText(email) || !email.matches(EMAIL_REGEX)) {
            throw new InvalidEmailException("이메일 형식이 올바르지 않습니다.");
        }
    }

    public void changePassword(String password, RoleCheck roleCheck) {
        if (!roleCheck.hasRole(MASTER) && !roleCheck.isMine(this.id)) {
            throw new UnauthorizedPasswordChangeException();
        }
        validatePassword(password);
    }

    public void promoteToDeliveryManager(UUID hubId, int sequence) {
        if (this.userRole != UserRole.HUB_DELIVERY_MANAGER &&
                this.userRole != UserRole.COMPANY_DELIVERY_MANAGER) {
            throw new CannotPromoteToDeliveryManagerException("배송 담당 권한이 없는 유저입니다.");
        } else {
            this.deliveryManager = DeliveryManager.create(this.userRole, hubId, sequence);
        }
    }

}
