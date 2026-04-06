package org.iimsa.userservice.domain.event;

import org.iimsa.userservice.domain.model.User;

/**
 * 도메인 이벤트 발행 인터페이스 Application Service는 이 인터페이스만 알고, 실제 Kafka 구현은 Infrastructure에서 담당합니다.
 */
public interface UserDeletedEvent {
    void deleted(User user);
}
