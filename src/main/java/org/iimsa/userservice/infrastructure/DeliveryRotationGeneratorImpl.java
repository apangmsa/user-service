package org.iimsa.userservice.infrastructure;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.iimsa.userservice.domain.service.hubdeliverymanager.DeliveryRotationGenerator;
import org.springframework.stereotype.Component;

/*
PostgreSQL의 Sequence 객체(delivery_sequence_seq)를 이용해
허브 배송 담당자의 delivery sequence를 "생성"한다.

Sequence는 DB에서 atomic하게 증가하기 때문에
동시 요청 상황에서도 중복 없이 순번을 생성할 수 있다.

PostgreSQL 시퀀스는 "연속성"을 보장하지 않습니다.

롤백, 서버 재시작, 캐시 등으로 언제든 구멍이 생길 수 있습니다.
*/
@Component
public class DeliveryRotationGeneratorImpl implements DeliveryRotationGenerator {

    @PersistenceContext
    private EntityManager em;

    @Override
    public int next() {
        return ((Number) em.createNativeQuery(
                        "SELECT nextval('delivery_sequence_seq')")
                .getSingleResult()).intValue();
    }
}
