package org.iimsa.userservice.presentation;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.iimsa.common.response.CommonResponse;
import org.iimsa.userservice.application.dto.query.UserQueryDto;
import org.iimsa.userservice.application.service.UserQueryService;
import org.iimsa.userservice.application.service.UserService;
import org.iimsa.userservice.presentation.dto.UserRequest;
import org.iimsa.userservice.presentation.dto.UserResponse;
import org.iimsa.userservice.presentation.dto.UserResponse.Info;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
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

    private final UserService userService; // 생성, 수정, 삭제
    private final UserQueryService userQueryService; // 조회 전용

    @Operation(
            summary = "신규 회원가입",
            description = "사용자 정보를 입력받아 시스템 사용자를 등록하고 외부 인증 서버(Keycloak)에 계정을 생성합니다."
    )
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public CommonResponse<UserResponse.SignUp> signUp(@RequestBody @Valid UserRequest.SignUp request) {
        UUID userId = userService.signUp(request.toDto());
        UserResponse.SignUp responseData = new UserResponse.SignUp(userId);
        return CommonResponse.success("회원가입이 완료되었습니다.", responseData);
    }

    @GetMapping("/{userId}")
    @ResponseStatus(HttpStatus.OK)
    public CommonResponse<UserResponse.Info> getUser(@PathVariable UUID userId) {
        UserResponse.Info responseData = userQueryService.getUser(userId);
        return CommonResponse.success("사용자 조회에 성공했습니다.", responseData);
    }

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public CommonResponse<Page<Info>> getUsers(UserQueryDto.Search search, Pageable pageable) {
        Page<UserResponse.Info> responseData = userQueryService.searchUsers(search, pageable);
        return CommonResponse.success("사용자 목록 조회에 성공했습니다.", responseData);
    }

}
