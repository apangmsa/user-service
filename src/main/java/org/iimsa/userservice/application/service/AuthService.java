package org.iimsa.userservice.application.service;

import org.iimsa.userservice.application.dto.TokenResult;
import org.iimsa.userservice.application.dto.command.LoginCommand;

public interface AuthService {
    TokenResult getToken(LoginCommand loginCommand);

    TokenResult refreshToken(String refreshToken);

    void logout(String refreshToken);
}
