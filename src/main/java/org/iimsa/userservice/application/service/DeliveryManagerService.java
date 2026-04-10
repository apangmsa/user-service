package org.iimsa.userservice.application.service;

import java.util.List;
import lombok.RequiredArgsConstructor;
import org.iimsa.userservice.application.dto.query.UserQueryRepository;
import org.iimsa.userservice.domain.model.User;
import org.iimsa.userservice.domain.service.hubdeliverymanager.RoundRobinCounter;
import org.iimsa.userservice.presentation.dto.DeliveryManagerSequenceResponse;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class DeliveryManagerService {

    private final UserQueryRepository userQueryRepository;
    private final RoundRobinCounter counter;

    public DeliveryManagerSequenceResponse getNext() {

        /* 만약 허브마다 10명 방식으로 바뀌면 hubId로 받아오기, 시퀀스 => 테이블로 관리 :
         1. 허브 서비스에서 허브 생성 이벤트 발행, hub.created { hubId: "uuid-A" }
         2. 유저 서비스가 이벤트 수신 → 카운터 행 자동 생성 INSERT INTO hub_rotation_counter (hub_id, counter) VALUES ('uuid-A', 0)*/
        List<User> managers = userQueryRepository.findHubDeliveryManagers();
        int size = managers.size();

        if (size == 0) {
            throw new IllegalStateException("배송 담당자가 없습니다.");
        }

        int next = counter.next();
        int index = (next - 1) % size;

        User selected = managers.get(index);

        return new DeliveryManagerSequenceResponse(
                selected.getId(),
                selected.getUsername(),
                selected.getSlackId(),
                selected.getDeliveryManager().getSequence()
        );
    }
}
