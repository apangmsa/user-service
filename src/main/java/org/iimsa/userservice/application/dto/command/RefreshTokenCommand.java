package org.iimsa.userservice.application.dto.command;

import org.iimsa.userservice.presentation.dto.RefreshTokenRequest;

public record RefreshTokenCommand(
        String refreshToken
) {
    public static RefreshTokenCommand from(RefreshTokenRequest request) {
        return new RefreshTokenCommand(request.refreshToken())
                ;
    }
}
