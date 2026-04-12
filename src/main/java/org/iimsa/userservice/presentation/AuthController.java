package org.iimsa.userservice.presentation;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.iimsa.userservice.application.dto.TokenResult;
import org.iimsa.userservice.application.dto.command.LoginCommand;
import org.iimsa.userservice.application.service.AuthService;
import org.iimsa.userservice.presentation.dto.TokenRequest;
import org.iimsa.userservice.presentation.dto.TokenResponse;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
@Tag(name = "인증 API", description = "인증 토큰 발급 및 갱신, 로그아웃 API")
public class AuthController {
    private final AuthService authService;

    @Operation(
            summary = "인증 토큰 발급",
            description = "이메일과 비밀번호를 사용하여 인증을 수행하고, Access/Refresh 토큰을 발급받습니다."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "인증 성공",
                    content = @Content(schema = @Schema(implementation = TokenResponse.class))),
            @ApiResponse(responseCode = "401", description = "인증 실패 (아이디/비밀번호 불일치)",
                    content = @Content),
            @ApiResponse(responseCode = "502", description = "인증 서버(Keycloak) 통신 장애",
                    content = @Content)
    })
    @PostMapping("/token")
    public TokenResponse token(@RequestBody @Valid TokenRequest request) {
        log.info("인증 토큰 발급 요청: {}", request.email());
        TokenResult tokenResult =
                authService.getToken(LoginCommand.from(request));
        return TokenResponse.from(tokenResult);
    }

}
