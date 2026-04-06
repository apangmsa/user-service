package org.iimsa.userservice.infrastructure;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import lombok.RequiredArgsConstructor;
import org.iimsa.userservice.domain.service.hubdeliverymanager.RoundRobinCounter;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class DbRoundRobinCounter implements RoundRobinCounter {

    @PersistenceContext
    private EntityManager em;

    @Override
    public int next() {
        return ((Number) em.createNativeQuery(
                        "SELECT nextval('delivery_rotation_seq')")
                .getSingleResult()).intValue();
    }
}
