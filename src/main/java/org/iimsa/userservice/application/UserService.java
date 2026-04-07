package org.iimsa.userservice.application;

import jakarta.transaction.Transactional;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.iimsa.userservice.application.dto.UserServiceDto;
import org.iimsa.userservice.domain.exception.UserNotFoundException;
import org.iimsa.userservice.domain.model.User;
import org.iimsa.userservice.domain.repository.UserRepository;
import org.iimsa.userservice.domain.service.hubdeliverymanager.DeliveryRotationGenerator;
import org.iimsa.userservice.domain.service.hubdeliverymanager.HubProvider;
import org.iimsa.userservice.domain.service.identity.IdentityProvider;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserService {

    private final IdentityProvider identityProvider;
    private final UserRepository userRepository;
    private final DeliveryRotationGenerator rotationGenerator;
    private final HubProvider hubProvider;
    // private final RoleCheck roleCheck;

    @Transactional
    public UUID signUp(UserServiceDto.SignUp data) {
        UUID userId = identityProvider.register(data.email(), data.password());
        try {
            User user = User.create(
                    userId,
                    data.name(),
                    data.email(),
                    data.slackId(),
                    data.requestedRole(),
                    data.associateName()
            );
            return userRepository.save(user).getId();
        } catch (Exception e) {
            // 보상 트랜잭션 - 외부인증 시스템 역시 계정 삭제
            identityProvider.withdraw(userId);
            throw e;
        }
    }

    @Transactional
    public void approve(UUID userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(UserNotFoundException::new);

        int sequence = rotationGenerator.next();

        // user.promoteToDeliveryManager(hubId, sequence);
    }
/*
    @Transactional
    public void changePassword(UUID userId, String password) {
        User user = userRepository.findById(userId)
                .orElseThrow(UserNotFoundException::new);

        user.changePassword(password, roleCheck);
        identityProvider.changePassword(userId, password);
    }*/
}
