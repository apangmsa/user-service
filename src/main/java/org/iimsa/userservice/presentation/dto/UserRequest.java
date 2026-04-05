package org.iimsa.userservice.presentation.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.AccessLevel;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.iimsa.userservice.application.dto.UserServiceDto;
import org.iimsa.userservice.domain.model.Role;
import org.iimsa.userservice.domain.model.User;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class UserRequest {
    @Data
    @Schema(description = "회원가입 요청 데이터")
    public static class SignUp {

        @Schema(description = "사용자 이름", example = "이용교", requiredMode = Schema.RequiredMode.REQUIRED)
        @NotBlank(message = "이름(name)은 필수 입력 항목입니다.")
        private String name;

        @Schema(description = "비밀번호 (영문, 숫자, 특수문자 포함 8~20자)", example = "Password123!", requiredMode = Schema.RequiredMode.REQUIRED)
        @NotBlank(message = "비밀번호(password)는 필수 입력 항목입니다.")
        @Pattern(regexp = User.PASSWORD_REGEX,
                message = "비밀번호는 영문, 숫자, 특수문자를 포함하여 8~20자여야 합니다.")
        private String password;

        @Schema(description = "이메일 주소", example = "yonggyo@spartahub.com", requiredMode = Schema.RequiredMode.REQUIRED)
        @NotBlank(message = "이메일(email)은 필수 입력 항목입니다.")
        @Email(message = "유효한 이메일 형식이 아닙니다.")
        private String email;

        @Schema(description = "신청된 사용자 역할 (MASTER, HUB_MANAGER, HUB_DELIVERY_MANAGER, COMPANY_MANAGER, COMPANY_DELIVERY_MANAGER)",
                example = "COMPANY_MANAGER", requiredMode = Schema.RequiredMode.REQUIRED)
        @NotBlank(message = "신청된 사용자 역할(requestedRole)은 필수 항목입니다.")
        @Pattern(regexp = "MASTER|HUB_MANAGER|HUB_DELIVERY_MANAGER|COMPANY_MANAGER|COMPANY_DELIVERY_MANAGER",
                message = "유효한 사용자 역할이 아닙니다. 유효한 사용자 : MASTER|HUB_MANAGER|HUB_DELIVERY_MANAGER|COMPANY_MANAGER|COMPANY_DELIVERY_MANAGER")
        private String requestedRole;

        @Schema(description = "슬랙 ID", example = "U12345678", requiredMode = Schema.RequiredMode.REQUIRED)
        @NotBlank(message = "슬랙 ID(slackId)는 알림 수신을 위해 필수입니다.")
        private String slackId;

        @Schema(description = "소속명", example = "옥천허브", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
        private String associateName;

        // 응용 계층으로 전달하기 위한 변환 메서드
        public UserServiceDto.SignUp toDto() {
            return new UserServiceDto.SignUp(
                    name,
                    password,
                    email,
                    slackId,
                    Role.valueOf(this.requestedRole.toUpperCase()),
                    associateName
            );
        }

    }
}
