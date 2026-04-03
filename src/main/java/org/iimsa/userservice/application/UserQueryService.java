package org.iimsa.userservice.application;

import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.iimsa.userservice.domain.model.User;
import org.iimsa.userservice.domain.query.UserQueryDto;
import org.iimsa.userservice.domain.query.UserQueryRepository;
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
                .orElseThrow(
                        () -> new IllegalArgumentException("사용자를 찾을 수 없습니다. ID: " + id)); // 실무에서는 Custom Exception 권장

        return mapToInfoDto(user);
    }

    // 2. 전체 조건 검색 (페이징)
    public Page<Info> searchUsers(UserQueryDto.Search search, Pageable pageable) {
        // Page 객체의 map() 메서드를 사용하여 Page<User>를 Page<UserResponse.Info>로 우아하게 변환합니다.
        return userQueryRepository.findAll(search, pageable)
                .map(this::mapToInfoDto);
    }

    // 3. 해당 허브에서 출발했거나/ 출발하는 기사 조회
    public Page<UserResponse.Info> searchDeleveryManagersByHub(UUID hubId, UserQueryDto.Search search,
                                                               Pageable pageable) {
        return userQueryRepository.findAllByHubId(hubId, search, pageable)
                .map(this::mapToInfoDto);
    }

    // ==============================================================================
    // 💡 Entity -> DTO 변환 메서드 (Mapper)
    // ==============================================================================


    private UserResponse.Info mapToInfoDto(User user) {
        UUID hubId = null;
        Integer sequence = null;

        // DeliveryManager(임베디드 타입)가 Null이 아닐 경우에만 데이터 안전하게 추출
        if (user.getDeliveryManager() != null) {
            hubId = user.getDeliveryManager().getHubId();
            sequence = user.getDeliveryManager().getSequence();
        }

        return UserResponse.Info.builder()
                .id(user.getId())
                // 엔티티의 필드명(username, userRole)과 DTO의 필드명(name, role) 매핑
                .name(user.getUsername())
                .email(user.getEmail())
                .role(user.getUserRole())
                .slackId(user.getSlackId())
                .hubId(hubId)
                .deliveryRotationOrder(sequence)

                // TODO : 허브 이름(hubName)이나 업체명(companyName)은 User DB에 없어 일단 null로 처리
                .hubName(null)
                .companyId(null)
                .companyName(null)

                .status(user.getUserStatus())
                .build();
    }
}
