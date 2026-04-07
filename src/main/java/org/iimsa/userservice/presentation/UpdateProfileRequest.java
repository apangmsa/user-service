package org.iimsa.userservice.presentation;

public record UpdateProfileRequest(
        // 본인 수정 가능
        String username,
        String slackId
) {
}
