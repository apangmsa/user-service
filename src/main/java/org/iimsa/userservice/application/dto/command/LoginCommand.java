package org.iimsa.userservice.application.dto.command;

import org.iimsa.userservice.presentation.dto.TokenRequest;

public record LoginCommand(
        String loginId,
        String password
) {
    public static LoginCommand from(TokenRequest request) {
        return new LoginCommand(
                request.email(),
                request.password()
        );
    }
}
