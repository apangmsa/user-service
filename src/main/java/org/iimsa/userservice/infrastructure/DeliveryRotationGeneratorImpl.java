package org.iimsa.userservice.infrastructure;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.iimsa.userservice.domain.service.hubdeliverymanager.DeliveryRotationGenerator;
import org.springframework.stereotype.Component;

/*
PostgreSQL의 Sequence 객체(delivery_rotation_seq)를 이용해
허브 배송 담당자의 delivery sequence를 생성한다.

Sequence는 DB에서 atomic하게 증가하기 때문에
동시 요청 상황에서도 중복 없이 순번을 생성할 수 있다.
*/
@Component
public class DeliveryRotationGeneratorImpl implements DeliveryRotationGenerator {

    @PersistenceContext
    private EntityManager em;

    @Override
    public int next() {
        return ((Number) em.createNativeQuery(
                        "SELECT nextval('delivery_rotation_seq')")
                .getSingleResult()).intValue();
    }
}
