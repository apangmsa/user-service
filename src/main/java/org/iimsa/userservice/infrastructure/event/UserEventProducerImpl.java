package org.iimsa.userservice.infrastructure.event;

import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.iimsa.common.event.Events;
import org.iimsa.userservice.domain.event.UserEventProducer;
import org.iimsa.userservice.domain.event.payload.UserDeletedPayload;
import org.iimsa.userservice.domain.model.User;
import org.iimsa.userservice.infrastructure.kafka.UserTopicProperties;
import org.slf4j.MDC;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

@Component
@RequiredArgsConstructor
@EnableConfigurationProperties(UserTopicProperties.class)
public class UserEventProducerImpl implements UserEventProducer {

    private final UserTopicProperties properties;

    @Override
    public void approved(User user) {
        // 추후 구현
    }

    @Override
    public void deleted(User user) {
        Events.trigger(
                getTraceId(),                     // correlationId
                "USER",                           // domainType
                user.getId().toString(),          // domainId (파티션 키)
                properties.deleted(),             // eventType = 토픽명
                UserDeletedPayload.from(user)     // payload
        );
    }

    @Override
    public void updated(User user) {
        // 나중 구현
    }

    private String getTraceId() {
        String traceId = MDC.get("traceId");
        return StringUtils.hasText(traceId) ? traceId : UUID.randomUUID().toString();
    }
}
