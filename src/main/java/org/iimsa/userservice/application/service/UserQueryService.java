package org.iimsa.userservice.application.service;

import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.iimsa.userservice.application.dto.query.UserQueryDto;
import org.iimsa.userservice.application.dto.query.UserQueryRepository;
import org.iimsa.userservice.domain.exception.UserNotFoundException;
import org.iimsa.userservice.domain.model.User;
import org.iimsa.userservice.presentation.dto.UserResponse;
import org.iimsa.userservice.presentation.dto.UserResponse.Info;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UserQueryService {

    private final UserQueryRepository userQueryRepository;

    // 1. 단건 조회
    public UserResponse.Info getUser(UUID id) {
        User user = userQueryRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException("사용자를 찾을 수 없습니다. ID: " + id));
        return mapToInfoDto(user);
    }

    // 2. 전체 조건 검색 (페이징)
    public Page<Info> searchUsers(UserQueryDto.Search search, Pageable pageable) {
        return userQueryRepository.findAll(search, pageable)
                .map(this::mapToInfoDto);
    }

    // ======================Entity -> DTO 변환 메서드 (Mapper)=============================

    private UserResponse.Info mapToInfoDto(User user) {
        UUID hubId = null; // DeliveryManager, HubManager 공통

        // DeliveryManager 정보
        Integer deliverySequence = null;
        if (user.getDeliveryManager() != null) {
            hubId = user.getDeliveryManager().getHubId();
            deliverySequence = user.getDeliveryManager().getSequence();
        }

        // CompanyManager 정보
        UUID companyId = null;
        String companyName = null;
        if (user.getCompanyManager() != null) {
            companyId = user.getCompanyManager().getCompanyId();
            // companyName = user.getCompanyManager().getCompanyName(); // 예시
        }

        // HubManager 정보
        String hubName = null;
        if (user.getHubManager() != null) {
            hubId = user.getHubManager().getHubId();
            // hubName = user.getHubManager().getHubName(); // 예시
        }

        return UserResponse.Info.builder()
                .id(user.getId())
                .name(user.getUsername())
                .email(user.getEmail())
                .slackId(user.getSlackId())
                .requestedRole(user.getRequestedRole())
                .role(user.getRole())
                .associateName(user.getAssociateName())
                .status(user.getStatus())
                .hubId(hubId)
                .companyId(companyId)
                .deliverySequence(deliverySequence)
                .build();
    }
}
