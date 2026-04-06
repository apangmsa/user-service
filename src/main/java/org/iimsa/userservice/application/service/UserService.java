package org.iimsa.userservice.application.service;

import jakarta.transaction.Transactional;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.iimsa.common.exception.BadRequestException;
import org.iimsa.userservice.application.dto.command.DeleteUserCommand;
import org.iimsa.userservice.application.dto.result.UserServiceDto;
import org.iimsa.userservice.domain.event.UserEventProducer;
import org.iimsa.userservice.domain.exception.InvalidUserException;
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
    private final HubProvider hubProvider;

    private final UserEventProducer userEventProducer;

    private final UserRepository userRepository;
    private final DeliveryRotationGenerator rotationGenerator;
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
    public void deleteUser(DeleteUserCommand command) {
        User user = userRepository.findById(command.targetUserId())
                .orElseThrow(UserNotFoundException::new);

        try {

            user.delete(command.deletedBy());
        } catch (InvalidUserException e) {
            throw new BadRequestException(e.getMessage());
        }
        // @Transactional 환경에서는 JPA 더티 체킹이 자동으로 UPDATE 쿼리를 날려줌
        // userRepository.save(user);
        // 이벤트 발행 (Kafka 또는 RabbitMQ)
        userEventProducer.deleted(user);
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
