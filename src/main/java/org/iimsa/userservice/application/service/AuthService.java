package org.iimsa.userservice.application.service;

import org.iimsa.userservice.application.dto.TokenResult;
import org.iimsa.userservice.application.dto.command.LoginCommand;
import org.iimsa.userservice.application.dto.command.RefreshTokenCommand;

public interface AuthService {
    TokenResult getToken(LoginCommand loginCommand);

    TokenResult refreshToken(RefreshTokenCommand refreshTokenCommand);

    void logout(String refreshToken);
}
