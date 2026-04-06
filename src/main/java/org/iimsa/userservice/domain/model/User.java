package org.iimsa.userservice.domain.model;

import static org.iimsa.userservice.domain.model.Status.PENDING;

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
import org.iimsa.userservice.domain.exception.InvalidEmailException;
import org.iimsa.userservice.domain.exception.InvalidPasswordException;
import org.iimsa.userservice.domain.exception.InvalidUserException;
import org.springframework.util.StringUtils;

@Builder(access = AccessLevel.PRIVATE)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@Entity
@Table(name = "p_user")
public class User extends BaseEntity {

    public static final String PASSWORD_REGEX =
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

    @Column(length = 25, name = "requested_role")
    @Enumerated(EnumType.STRING)
    private Role requestedRole;

    @Column(length = 25, name = "role")
    @Enumerated(EnumType.STRING)
    private Role role;

    @Column(length = 100, name = "associate_name")
    private String associateName;

    @Column(length = 20, name = "status")
    @Enumerated(EnumType.STRING)
    private Status status;

    @Embedded
    private HubManager hubManager;

    @Embedded
    private CompanyManager companyManager;

    @Embedded
    private DeliveryManager deliveryManager;

    public static User create(UUID id, String username, String email, String slackId, Role requestedRole,
                              String associateName) {
        validateEmail(email);
        validateEmail(slackId);

        return User.builder()
                .id(id)
                .username(username)
                .email(email)
                .slackId(slackId)
                .requestedRole(requestedRole)
                .status(PENDING)
                .associateName(associateName)
                .build();
    }

    public void delete(String deletedByUsername) {
        if (this.deletedAt != null) {
            throw new InvalidUserException("이미 삭제된 사용자입니다.");
        }
        // 공통 모듈의 BaseEntity.delete()메서드 사용
        super.delete(deletedByUsername);
        // status도 REJECTED로 변경해서 혹시 모를 로그인 차단
        this.status = Status.REJECTED;
    }

    public boolean isDeleted() {
        return this.deletedAt != null;
    }

    // ===================유효성 검사
    private static void validatePassword(String password) {
        if (!StringUtils.hasText(password) || !password.matches(PASSWORD_REGEX)) {
            throw new InvalidPasswordException("비밀번호는 영문, 숫자, 특수문자를 포함하여 8~20자여야 합니다.");
        }
    }

    private static void validateEmail(String email) {
        if (!StringUtils.hasText(email) || !email.matches(EMAIL_REGEX)) {
            throw new InvalidEmailException("이메일 형식이 올바르지 않습니다.");
        }
    }
}
