package org.iimsa.userservice.presentation.dto;

public record UpdateProfileRequest(
        // 본인 수정 가능
        String name,
        String slackId
) {
}
