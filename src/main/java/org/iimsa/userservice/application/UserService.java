package org.iimsa.userservice.application;

import jakarta.transaction.Transactional;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.iimsa.userservice.application.dto.UserServiceDto;
import org.iimsa.userservice.domain.exception.UserNotFoundException;
import org.iimsa.userservice.domain.model.User;
import org.iimsa.userservice.domain.service.hubdeliverymanager.HubProvider;
import org.iimsa.userservice.domain.service.identity.IdentityProvider;
import org.iimsa.userservice.domain.service.identity.RoleCheck;
import org.iimsa.userservice.infrastructure.persistence.jpa.JpaUserRepository;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserService {

    private final IdentityProvider identityProvider;
    private final JpaUserRepository userRepository;
    private final HubProvider hubProvider;
    private final RoleCheck roleCheck;

    /**
     * 1. 외부 인증시스템에 계정 생성 및 UUID 발급 2. 발급받은 UUID로 User 엔티티 생성 및 DB 저장 3. DB 저장 실패 시 Keycloak에 생성된 계정 롤백(Withdraw)
     */
    @Transactional
    public UUID signUp(UserServiceDto.SignUp data) {

        UUID userId = identityProvider.register(data.getEmail(), data.getPassword());

        try {
            User user = User.create(
                    userId,
                    data.getName(),
                    data.getEmail(),
                    data.getRole(),
                    data.getHubId()
            );
            return userRepository.save(user).getId();
        } catch (Exception e) {
            // 보상 트랜잭션 - 외부인증 시스템 역시 계정 삭제
            identityProvider.withdraw(userId);
            throw e;
        }
    }

    @Transactional
    public void changePassword(UUID userId, String password) {
        User user = userRepository.findById(userId).orElseThrow(UserNotFoundException::new);
        // 롤 체크 (본인만 가능)
        identityProvider.changePassword(userId, password);

    }
}
