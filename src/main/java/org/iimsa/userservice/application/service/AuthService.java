package org.iimsa.userservice.application.service;

import org.iimsa.userservice.application.dto.AuthTokenResult;

public interface AuthService {
    AuthTokenResult getToken(String email, String password);

    AuthTokenResult refreshToken(String refreshToken);

    void logout(String refreshToken);
}
