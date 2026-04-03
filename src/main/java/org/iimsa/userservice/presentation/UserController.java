package org.iimsa.userservice.presentation;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.iimsa.common.response.CommonResponse;
import org.iimsa.userservice.application.UserQueryService;
import org.iimsa.userservice.application.UserService;
import org.iimsa.userservice.domain.query.UserQueryDto;
import org.iimsa.userservice.presentation.dto.UserRequest;
import org.iimsa.userservice.presentation.dto.UserResponse;
import org.iimsa.userservice.presentation.dto.UserResponse.Info;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.web.PageableDefault;
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

    private final UserService userService; // 쓰기 담당
    private final UserQueryService userQueryService; // 읽기 담당

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

    @Operation(summary = "사용자 단건 상세 조회", description = "ID를 통해 특정 사용자의 상세 정보를 조회합니다.")
    @GetMapping("/{userId}")
    public CommonResponse<UserResponse.Info> getUser(@PathVariable UUID userId) {
        return CommonResponse.success(userQueryService.getUser(userId));
    }

    @Operation(summary = "사용자 목록 조회", description = "검색 조건과 페이징을 이용해 사용자 목록을 조회합니다.")
    @GetMapping
    public CommonResponse<Page<Info>> getUsers(
            UserQueryDto.Search search, // Query String이 자동으로 DTO에 매핑됨
            @PageableDefault(size = 10, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable
    ) {
        return CommonResponse.success(userQueryService.searchUsers(search, pageable));
    }

    @Operation(summary = "업체 배송 담당자 목록 조회", description = "검색 조건과 페이징을 이용해 허브에 속한 업체 배송 담당자 목록을 조회합니다.")
    @GetMapping("/hubs/{hubId}")
    public CommonResponse<Page<Info>> getUsersByHub(
            @PathVariable UUID hubId,
            UserQueryDto.Search search,
            @PageableDefault(size = 10, sort = "createdAt", direction = Direction.DESC) Pageable pageable
    ) {
        // 허브별 배송 담당자 전용 조회 메서드 호출
        return CommonResponse.success(
                userQueryService.searchDeleveryManagersByHub(hubId, search, pageable)
        );
    }
}
