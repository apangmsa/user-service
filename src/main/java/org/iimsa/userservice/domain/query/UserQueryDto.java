package org.iimsa.userservice.domain.query;

import java.util.List;
import java.util.UUID;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.iimsa.userservice.domain.model.Role;
import org.iimsa.userservice.domain.model.Status;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class UserQueryDto {

    /**
     * 검색 필드 설계 기준
     * <p>
     * List 타입: 여러 개 선택 가능 - ids, emails, slackIds, requestedRoles, roles, hubIds, companyIds
     * <p>
     * 단일 타입: 한 번에 하나만 의미 - name, associateName, status, keyword
     * <p>
     * 판단 기준: "사용자가 화면에서 동시에 여러 개 선택할 수 있는가?"
     * <p>
     * 단일로 구현 후 필요 시 List로 확장, 자주 쓰이는 필드 위주로 전용 쿼리를 만들어가면서 불필요한 필드는 제거/비활성화
     */
    @Setter
    @Getter
    @Builder
    public static class Search {
        private List<UUID> ids;
        private String name;
        private List<String> emails;
        private List<String> slackIds;
        private List<Role> requestedRoles;
        private List<Role> roles;
        private String associateName;
        private Status status;
        private List<UUID> hubIds;
        private List<UUID> companyIds;
        private String keyword; // 키워드 검색
    }
}
