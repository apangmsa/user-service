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

/**
 * Info : 모든 정보 표시 (null 포함) 추후 조회 용도에 따라 DTO 분기 CompanyDeliveryInfo HubDeliveryInfo InfoToApprove
 *
 */
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
        @Schema(description = "이메일")
        private String email;
        @Schema(description = "슬랙 ID (이메일 형식s)")
        private String slackId;
        @Schema(description = "사용자 희망 역할")
        private Role requestedRole;
        @Schema(description = "사용자 역할 (MASTER, HUB_MANAGER, HUB_DELIVERY_MANAGER, COMPANY_MANAGER, COMPANY_DELIVERY_MANAGER)")
        private Role role;
        @Schema(description = "소속명")
        private String associateName;
        @Schema(description = "사용자 승인 상태 (PENDING, APPROVED, REJECTED)")
        private Status status;
        @Schema(description = "소속 허브 ID")
        private UUID hubId;
        @Schema(description = "소속 업체 ID")
        private UUID companyId;
        @Schema(description = "배송 순번")
        private Integer deliverySequence;
    }
}
