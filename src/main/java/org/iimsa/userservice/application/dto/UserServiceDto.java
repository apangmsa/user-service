package org.iimsa.userservice.application.dto;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.NoArgsConstructor;
import org.iimsa.userservice.domain.model.Role;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class UserServiceDto {

    @Builder
    public record SignUp(
            String name,
            String password,
            String email,
            String slackId,
            Role requestedRole,
            String associateName
    ) {

    }
}
