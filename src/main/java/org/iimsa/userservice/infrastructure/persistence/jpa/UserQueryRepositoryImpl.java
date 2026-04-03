package org.iimsa.userservice.infrastructure.persistence.jpa;

import static org.iimsa.userservice.domain.model.QUser.user;

import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.core.types.dsl.StringPath;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.iimsa.userservice.domain.model.User;
import org.iimsa.userservice.domain.model.UserRole;
import org.iimsa.userservice.domain.query.UserQueryDto;
import org.iimsa.userservice.domain.query.UserQueryRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.support.PageableExecutionUtils;
import org.springframework.stereotype.Repository;
import org.springframework.util.StringUtils;

@Repository
@RequiredArgsConstructor
public class UserQueryRepositoryImpl implements UserQueryRepository {

    private final JPAQueryFactory queryFactory;

    // 공통 페이징 처리 로직
    private Page<User> getPage(UserQueryDto.Search search, Pageable pageable, BooleanExpression baseCondition) {
        BooleanBuilder builder = createSearchCondition(search);

        // BaseEntity 반영: 삭제되지 않은(deletedAt == null) 데이터만 조회
        builder.and(user.deletedAt.isNull());

        if (baseCondition != null) {
            builder.and(baseCondition);
        }

        // 실제 데이터 조회 쿼리
        List<User> content = queryFactory
                .selectFrom(user)
                .where(builder)
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .orderBy(user.createdAt.desc()) // BaseEntity의 createdAt 사용
                .fetch();

        // 카운트 쿼리
        JPAQuery<Long> countQuery = queryFactory
                .select(user.count())
                .from(user)
                .where(builder);

        // PageableExecutionUtils를 사용하면 첫 페이지이거나 마지막 페이지일 때 불필요한 카운트 쿼리를 생략하여 성능이 향상됨
        return PageableExecutionUtils.getPage(content, pageable, countQuery::fetchOne);
    }

    // 동적 검색 조건 생성 로직
    private BooleanBuilder createSearchCondition(UserQueryDto.Search search) {
        BooleanBuilder builder = new BooleanBuilder();

        if (search == null) {
            return builder;
        }

        if (search.getIds() != null && !search.getIds().isEmpty()) {
            builder.and(user.id.in(search.getIds()));
        }

        if (search.getUserRole() != null) {
            builder.and(user.userRole.eq(search.getUserRole()));
        }

        if (search.getHubIds() != null && !search.getHubIds().isEmpty()) {
            builder.and(user.deliveryManager.hubId.in(search.getHubIds()));
        }

        if (search.getEmail() != null && !search.getEmail().isEmpty()) {
            builder.and(user.email.in(search.getEmail()));
        }

        if (StringUtils.hasText(search.getName())) {
            builder.and(user.username.containsIgnoreCase(search.getName()));
        }

        // 통합 키워드 검색 (UUID 타입 hubId를 StringPath로 강제 형변환하여 검색 지원)
        if (StringUtils.hasText(search.getKeyword())) {
            String keyword = search.getKeyword();
            StringPath hubIdStringPath = Expressions.stringPath(user.deliveryManager.hubId, "hubId");

            builder.and(
                    user.username.containsIgnoreCase(keyword)
                            .or(user.email.containsIgnoreCase(keyword))
                            .or(hubIdStringPath.containsIgnoreCase(keyword))
            );

        }
        if (search.getStatus() != null) {
            builder.and(user.userStatus.eq(search.getStatus()));
        }

        return builder;
    }

    @Override
    public Optional<User> findById(UUID id) {
        return Optional.ofNullable(
                queryFactory
                        .selectFrom(user)
                        .where(user.id.eq(id))
                        .fetchOne()
        );
    }

    @Override
    public Page<User> findAllByHubId(UUID hubId, UserQueryDto.Search search, Pageable pageable) {
        // 기본 조건(hubId 일치) + 동적 검색 조건
        return getPage(search, pageable, user.deliveryManager.hubId.eq(hubId));
    }

    @Override
    public Page<User> findAllByCompanyId(UUID companyId, UserQueryDto.Search search, Pageable pageable) {
        // TODO: 현재 User 엔티티 구조상 companyId 필드가 누락되어 있어 임시로 null 처리
        // 추후 CompanyManager 임베디드 객체 등이 추가되면 해당 필드로 조건을 수정해야 합니다.
        return getPage(search, pageable, null);
    }

    @Override
    public Page<User> findAllByRole(UserRole role, UserQueryDto.Search search, Pageable pageable) {
        return getPage(search, pageable, user.userRole.eq(role));
    }

    @Override
    public Page<User> findAll(UserQueryDto.Search search, Pageable pageable) {
        return getPage(search, pageable, null);
    }


}
