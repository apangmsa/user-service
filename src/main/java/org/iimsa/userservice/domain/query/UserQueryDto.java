package org.iimsa.userservice.domain.query;

import java.util.List;
import java.util.UUID;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.iimsa.userservice.domain.model.UserRole;
import org.iimsa.userservice.domain.model.UserStatus;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class UserQueryDto {
    @Setter
    @Getter
    @Builder
    public static class Search {
        private List<UUID> ids; // 회원 아이디 - 단일 또는 복수개 검색 가능
        private List<UUID> hubIds; // 허브 아이디 - 단일 또는 복수개 검색 가능
        private List<UUID> companyIds; // 업체 아이디 - 단일 또는 복수개 검색 가능
        private String name; // 회원이름
        private List<String> email; // 이메일
        private UserRole userRole;
        private String hubName; // 허브명
        private String companyName; // 업체명
        private UserStatus status;
        private String keyword; // 키워드(name + email + hubName + storeName 에서 키워드 검색)
    }
}
