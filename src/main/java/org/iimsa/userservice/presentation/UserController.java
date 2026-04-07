package org.iimsa.userservice.presentation;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.iimsa.common.response.CommonResponse;
import org.iimsa.userservice.application.dto.command.ApproveUserCommand;
import org.iimsa.userservice.application.dto.command.DeleteUserCommand;
import org.iimsa.userservice.application.dto.query.UserQueryDto.Search;
import org.iimsa.userservice.application.service.DeliveryManagerService;
import org.iimsa.userservice.application.service.UserQueryService;
import org.iimsa.userservice.application.service.UserService;
import org.iimsa.userservice.presentation.dto.ApproveRequest;
import org.iimsa.userservice.presentation.dto.DeliveryManagerSequenceResponse;
import org.iimsa.userservice.presentation.dto.UserRequest;
import org.iimsa.userservice.presentation.dto.UserResponse.Info;
import org.iimsa.userservice.presentation.dto.UserResponse.SignUp;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
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
    private final DeliveryManagerService deliveryManagerService; // 시퀀스 조회등 배송기사 관련

    @Operation(
            summary = "신규 회원가입",
            description = "사용자 정보를 입력받아 시스템 사용자를 등록하고 외부 인증 서버(Keycloak)에 계정을 생성합니다."
    )
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public CommonResponse<SignUp> signUp(@RequestBody @Valid UserRequest.SignUp request) {
        UUID userId = userService.signUp(request.toDto());
        SignUp responseData = new SignUp(userId);
        return CommonResponse.success("회원가입이 완료되었습니다.", responseData);
    }

    @GetMapping("/{userId}")
    @ResponseStatus(HttpStatus.OK)
    public CommonResponse<Info> getUser(@PathVariable UUID userId) {
        Info responseData = userQueryService.getUser(userId);
        return CommonResponse.success("사용자 조회에 성공했습니다.", responseData);
    }

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public CommonResponse<Page<Info>> getUsers(Search search, Pageable pageable) {
        Page<Info> responseData = userQueryService.searchUsers(search, pageable);
        return CommonResponse.success("사용자 목록 조회에 성공했습니다.", responseData);
    }

    @GetMapping("/next-sequence/hub-delivery")
    @ResponseStatus(HttpStatus.OK)
    public DeliveryManagerSequenceResponse getNextHubDeliver() {
        return deliveryManagerService.getNext();
    }

    @Operation(
            summary = "사용자 삭제 (MASTER 전용)",
            description = "소프트 삭제 처리 후 연관 서비스에 삭제 이벤트를 발행합니다."
    )
    @DeleteMapping("/{userId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public CommonResponse<Void> deleteUser(@PathVariable UUID userId) {
        userService.deleteUser(new DeleteUserCommand(userId, "삭제자@email.com"));
        return CommonResponse.success("사용자가 삭제되었습니다.", null);
    }

    @Operation(
            summary = "사용자 가입 승인 (MASTER 전용)",
            description = "가입 정보를 확인하고 사용자의 상태를 APPROVED로 변경합니다."
    )
    @PostMapping("/approve/{userId}")
    @ResponseStatus(HttpStatus.OK)
    public CommonResponse<Info> approveUser(
            @PathVariable UUID userId,
            @RequestBody ApproveRequest request
    ) {
        // TODO: approvedBy는 SecurityContext에서 추출 (임시로 하드코딩)
        String approvedBy = "승인자@email.com";

        Info responseData = userService.approve(
                new ApproveUserCommand(
                        userId,
                        request.requestedRole(),
                        request.hubId(),
                        request.companyId(),
                        approvedBy
                )
        );
        return CommonResponse.success(userId + "사용자를 승인했습니다.", responseData);
    }

    @Operation(
            summary = "사용자 가입 승인 (MASTER 전용)",
            description = "가입 정보를 확인하고 사용자의 상태를 APPROVED로 변경합니다."
    )
    @GetMapping("/reject/{userId}")
    @ResponseStatus(HttpStatus.OK)
    public CommonResponse<String> rejectUser(@PathVariable UUID userId) {
        userService.reject(userId);
        return CommonResponse.success(userId + "사용자의 승인을 거절했습니다.");
    }

}
