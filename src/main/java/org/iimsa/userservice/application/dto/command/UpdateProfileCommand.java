package org.iimsa.userservice.application.dto.command;

import java.util.UUID;

public record UpdateProfileCommand(
        UUID targetUserId,
        String username,
        String slackId,
        String updatedBy
) {
}
