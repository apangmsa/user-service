package org.iimsa.userservice.application.dto.command;

public record LoginCommand(
        String loginId,
        String password
) {
}
