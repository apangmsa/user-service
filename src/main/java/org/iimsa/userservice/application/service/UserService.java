package org.iimsa.userservice.application.service;

import jakarta.transaction.Transactional;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.iimsa.common.exception.BadRequestException;
import org.iimsa.userservice.application.dto.command.ApproveUserCommand;
import org.iimsa.userservice.application.dto.command.DeleteUserCommand;
import org.iimsa.userservice.application.dto.command.UpdateProfileCommand;
import org.iimsa.userservice.application.dto.command.UpdateRoleCommand;
import org.iimsa.userservice.application.dto.result.UserServiceResult;
import org.iimsa.userservice.domain.event.UserEventProducer;
import org.iimsa.userservice.domain.exception.InvalidUserException;
import org.iimsa.userservice.domain.exception.UserNotFoundException;
import org.iimsa.userservice.domain.model.Role;
import org.iimsa.userservice.domain.model.User;
import org.iimsa.userservice.domain.repository.UserRepository;
import org.iimsa.userservice.domain.service.hubdeliverymanager.DeliveryRotationGenerator;
import org.iimsa.userservice.domain.service.hubdeliverymanager.HubProvider;
import org.iimsa.userservice.domain.service.identity.IdentityProvider;
import org.iimsa.userservice.presentation.dto.UserResponse;
import org.iimsa.userservice.presentation.dto.UserResponse.Info;
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
    public UUID signUp(UserServiceResult.SignUp data) {
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
    public Info updateProfile(UpdateProfileCommand command) {
        User user = userRepository.findById(command.targetUserId())
                .orElseThrow(UserNotFoundException::new);

        try {

            user.updateProfile(command.username(), command.slackId());
        } catch (Exception e) {
            throw new BadRequestException(e.getMessage());
        }
        // 이름 변경 → Hub, Company, Delivery, Order에 알림
        userEventProducer.updated(user);

        return UserResponse.from(user);
    }

    @Transactional
    public Info updateRole(UpdateRoleCommand command) {
        User user = userRepository.findById(command.targetUserId())
                .orElseThrow(UserNotFoundException::new);

        if (command.hubId() != null) {
            hubProvider.get(command.hubId());
        }
        Integer sequence = isDeliveryManagerRole(command.role())
                ? rotationGenerator.next()
                : null;
        try {
            user.updateRole(command.role(), command.hubId(), command.companyId(), sequence);
        } catch (Exception e) {
            throw new BadRequestException(e.getMessage());
        }

        // 역할 변경 → 각 서비스에 알림
        userEventProducer.updated(user);
        
        return UserResponse.from(user);
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
    public Info approve(ApproveUserCommand command) {
        // 대상 사용자 조회
        User user = userRepository.findById(command.targetUserId())
                .orElseThrow(UserNotFoundException::new);

        // 외부 허브 유효성 검증 (hubId가 있을 때만)
        if (command.hubId() != null) {
            hubProvider.get(command.hubId()); // 허브 서비스 호출
        }

        // 시퀀스 생성 (배송 담당자 역할인 경우에만)
        Integer sequence = isDeliveryManagerRole(command.requestedRole())
                ? rotationGenerator.next()
                : null;

        try {
            user.approve(command.requestedRole(), command.hubId(), command.companyId(), sequence);
        } catch (InvalidUserException e) {
            throw new BadRequestException(e.getMessage());
        }

        // Kafka 이벤트 발행
        userEventProducer.approved(user);

        return UserResponse.from(user);
    }

    @Transactional
    public void reject(UUID userId) {
        // 대상 사용자 조회
        User user = userRepository.findById(userId)
                .orElseThrow(UserNotFoundException::new);
        user.reject();
    }

    private boolean isDeliveryManagerRole(Role role) {
        return role == Role.HUB_DELIVERY_MANAGER || role == Role.COMPANY_DELIVERY_MANAGER;
    }
}
