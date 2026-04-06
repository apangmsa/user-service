package org.iimsa.userservice.presentation.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import java.util.UUID;

public record DeliveryManagerSequenceResponse(
        @Schema(description = "배송 담당자 UUID")
        UUID userId,
        @Schema(description = "사용자 이름", example = "이용교")
        String name,
        @Schema(description = "슬랙 ID", example = "yonggyo@slack.com")
        String slackId,
        @Schema(description = "다음 배송 순번", example = "3")
        Integer nextSequence
) {
}
