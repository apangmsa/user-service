package org.iimsa.userservice.presentation.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import java.util.UUID;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.iimsa.userservice.domain.model.Role;
import org.iimsa.userservice.domain.model.Status;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class UserResponse {
    @AllArgsConstructor
    public static class SignUp {
        @Schema(description = "생성된 사용자 ID")
        public UUID id;
    }

    @Getter
    @Builder
    @AllArgsConstructor
    @Schema(description = "사용자 상세 정보 응답")
    public static class Info {
        @Schema(description = "사용자 ID")
        private UUID id;
        @Schema(description = "사용자 이름")
        private String name;
        @Schema(description = "사용자 역할")
        private Role role;
        @Schema(description = "이메일")
        private String email;
        @Schema(description = "슬랙 ID")
        private String slackId;
        @Schema(description = "소속 허브 ID")
        private UUID hubId;
        @Schema(description = "소속 허브명")
        private String hubName;
        @Schema(description = "배송 순번")
        private Integer deliveryRotationOrder;
        @Schema(description = "소속 업체 ID")
        private UUID companyId;
        @Schema(description = "소속 업체명")
        private String companyName;
        @Schema(description = "사용자 승인 상태 (PENDING, APPROVED, REJECTED)")
        private Status status;
    }
}
