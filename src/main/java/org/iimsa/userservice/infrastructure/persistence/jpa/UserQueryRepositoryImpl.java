package org.iimsa.userservice.infrastructure.persistence.jpa;

import static org.iimsa.userservice.domain.model.QUser.user;

import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.iimsa.userservice.domain.model.User;
import org.iimsa.userservice.domain.query.UserQueryDto.Search;
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

    // ===================== 공통 페이징 처리 =====================
    private Page<User> getPage(Search search, Pageable pageable, BooleanExpression baseCondition) {
        BooleanBuilder builder = createSearchCondition(search);

        // BaseEntity 반영: 삭제되지 않은 데이터만 조회
        builder.and(user.deletedAt.isNull());

        if (baseCondition != null) {
            builder.and(baseCondition);
        }

        List<User> content = queryFactory
                .selectFrom(user)
                .where(builder)
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .orderBy(user.createdAt.desc())
                .fetch();

        JPAQuery<Long> countQuery = queryFactory
                .select(user.count())
                .from(user)
                .where(builder);

        return PageableExecutionUtils.getPage(content, pageable, countQuery::fetchOne);
    }

    // ===================== 동적 검색 조건 생성 =====================
    private BooleanBuilder createSearchCondition(Search search) {
        BooleanBuilder builder = new BooleanBuilder();
        if (search == null) {
            return builder;
        }

        if (search.getIds() != null && !search.getIds().isEmpty()) {
            builder.and(user.id.in(search.getIds()));
        }

        if (search.getRoles() != null && !search.getRoles().isEmpty()) {
            builder.and(user.role.in(search.getRoles()));
        }

        if (search.getRequestedRoles() != null && !search.getRequestedRoles().isEmpty()) {
            builder.and(user.requestedRole.in(search.getRequestedRoles()));
        }

        if (search.getHubIds() != null && !search.getHubIds().isEmpty()) {
            builder.and(user.deliveryManager.hubId.in(search.getHubIds()));
        }

        if (search.getCompanyIds() != null && !search.getCompanyIds().isEmpty()) {
            builder.and(user.companyManager.companyId.in(search.getCompanyIds()));
        }

        if (search.getEmails() != null && !search.getEmails().isEmpty()) {
            builder.and(user.email.in(search.getEmails()));
        }

        if (StringUtils.hasText(search.getName())) {
            builder.and(user.username.containsIgnoreCase(search.getName()));
        }

        if (StringUtils.hasText(search.getKeyword())) {
            String keyword = search.getKeyword();
            builder.and(
                    user.username.containsIgnoreCase(keyword)
                            .or(user.email.containsIgnoreCase(keyword))
                            .or(user.slackId.containsIgnoreCase(keyword))
                            // UUID를 문자열 템플릿으로 감싸서 검색
                            .or(Expressions.stringTemplate("CAST({0} AS string)", user.deliveryManager.hubId)
                                    .containsIgnoreCase(keyword))
            );
        }

        if (search.getStatus() != null) {
            builder.and(user.status.eq(search.getStatus()));
        }

        return builder;
    }

    // ===================== 인터페이스 구현 =====================
    @Override
    public Optional<User> findById(UUID id) {
        return Optional.ofNullable(
                queryFactory.selectFrom(user)
                        .where(
                                user.id.eq(id),
                                user.deletedAt.isNull()
                        )
                        .fetchOne()
        );
    }

    @Override
    public Page<User> findAll(Search search, Pageable pageable) {
        return getPage(search, pageable, null);
    }

    @Override
    public Page<User> findAllByHubId(UUID hubId, Search search, Pageable pageable) {
        return getPage(search, pageable, user.deliveryManager.hubId.eq(hubId));
    }

    @Override
    public Page<User> findAllByCompanyId(UUID companyId, Search search, Pageable pageable) {
        return getPage(search, pageable, user.companyManager.companyId.eq(companyId));
    }


}
