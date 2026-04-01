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
import org.iimsa.common.exception.BadRequestException;
import org.iimsa.common.exception.ForbiddenException;
import org.iimsa.userservice.domain.service.identity.IdentityProvider;
import org.iimsa.userservice.domain.service.identity.RoleCheck;
import org.springframework.util.StringUtils;

@Builder(access = AccessLevel.PRIVATE)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@Entity
@Table(name = "p_user")
public class User extends BaseEntity {

    private static final String PASSWORD_REGEX = "^(?=.*[a-z])(?=.*[A-Z])(?=.*[0-9])(?=.*[^a-zA-Z0-9ㄱ-ㅎㅏ-ㅣ가-힣\\s])[a-zA-Z0-9ㄱ-ㅎㅏ-ㅣ가-힣\\s\\S]{8,20}$";
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
    public static User create(UUID id, String username, String email, UserRole role, UUID hubId) {
        // 1. 파라미터 검증 (null? 제약사항)
        // 이메일 ..
        UserBuilder builder = User.builder()
                .id(id)
                .username(username)
                .email(email)
                .userRole(role);
        if (role == UserRole.HUB_DELIVERY_MANAGER) {
            // 배송 매니저라면 객체를 임시로 생성해서 넣어줌 (내부에서 hubId null 체크 수행) sequence의 경우 승인시 결정
            builder.deliveryManager(DeliveryManager.create(role, hubId));
        } else {
            // 일반 유저라면 null로 세팅
            builder.deliveryManager(null);
        }

        return builder.build();
    }

    /**
     * 1. 허브 배송 담당자
     */
    public static User createHubDeliveryManager(UUID id, String username, UUID hubId, int sequence) {
        return User.builder()
                .id(id)
                .username(username)
                .userRole(UserRole.HUB_DELIVERY_MANAGER)
                .build();
    }

    /**
     * 2. 일반 유저 또는 관리자 생성 (배송 정보가 없는 경우)
     */
    public static User createGeneralUser(UUID id, String username, String email, UserRole role) {
        // 배송 담당자 권한을 이 메서드로 만들려고 하면 예외 처리 (방어 코드)
        if (role == UserRole.HUB_DELIVERY_MANAGER || role == UserRole.COMPANY_DELIVERY_MANAGER) {
            throw new IllegalArgumentException("배송 담당자는 전용 생성 메서드를 사용해야 합니다.");
        }

        return User.builder()
                .id(id)
                .username(username)
                .email(email)
                .userRole(role)
                .deliveryManager(null) // 배송 정보 없음
                .build();
    }

    public void changePassword(String password, RoleCheck roleCheck, IdentityProvider identityProvider) {
        // 권한 체크
        if (roleCheck.hasRole(MASTER) && !roleCheck.isMine(this.id)) {
            throw new ForbiddenException("비밀번호를 변경할 권한이 없습니다.");
        }
        validatePassword(password);

        // 외부 인증 서버 변경 요청
        identityProvider.changePassword(id, password);
    }

    // 비밀번호 유효성 검사
    private static void validatePassword(String password) {
        if (!StringUtils.hasText(password) || !password.matches(PASSWORD_REGEX)) {
            throw new BadRequestException("비밀번호는 영문, 숫자, 특수문자를 포함하여 8~20자여야 합니다.");
        }
    }

    private static void validateUsername(String loginId) {
        if (loginId == null || !loginId.matches(LOGIN_ID_REGEX)) {
            throw new BadRequestException("사용자명은 4~10자의 알파벳 소문자와 숫자로만 구성되어야 합니다.");
        }
    }

}