package org.iimsa.userservice.domain.query;

import java.util.Optional;
import java.util.UUID;
import org.iimsa.userservice.domain.model.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface UserQueryRepository {
    // 단일 조회
    Optional<User> findById(UUID id);

    // 모든 필터 조건 적용한 범용 검색 (검색 + 페이징)
    Page<User> findAll(UserQueryDto.Search search, Pageable pageable);

    // (선택) 자주 쓰는 특정 필드 조회용 편의 메서드
    Page<User> findAllByHubId(UUID hubId, UserQueryDto.Search search, Pageable pageable);

    Page<User> findAllByCompanyId(UUID companyId, UserQueryDto.Search search, Pageable pageable);
}
