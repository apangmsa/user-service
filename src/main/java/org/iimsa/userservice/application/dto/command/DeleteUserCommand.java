package org.iimsa.userservice.application.dto.command;

import java.util.UUID;

public record DeleteUserCommand(
        UUID targetUserId,
        String deletedBy
) {
}
