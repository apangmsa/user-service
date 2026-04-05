package org.iimsa.userservice.application.dto;

import java.util.UUID;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.iimsa.userservice.domain.model.Role;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class UserServiceDto {

    @Getter
    @Builder
    public static class SignUp {
        private final String name;
        private final String password;
        private final Role role;

        private final UUID hubId;
        private final UUID companyId;

        private final String email;
        private final String slackId;
    }
}
