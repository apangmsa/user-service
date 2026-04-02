package org.iimsa.userservice.presentation;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.iimsa.userservice.application.UserService;
import org.iimsa.userservice.presentation.dto.UserRequest;
import org.iimsa.userservice.presentation.dto.UserResponse;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
@Tag(name = "사용자 API", description = "사용자 계정 생성 및 관리 API")
public class UserController {

    private final UserService userService;

    @Operation(
            summary = "신규 회원가입",
            description = "사용자 정보를 입력받아 시스템 사용자를 등록하고 외부 인증 서버(Keycloak)에 계정을 생성합니다."
    )
    @PostMapping()
    @ResponseStatus(HttpStatus.CREATED)
    public UserResponse.SignUp signUp(@RequestBody @Valid UserRequest.SignUp request) {
        UUID userId = userService.signUp(request.toDto());

        return new UserResponse.SignUp(userId);
    }

}