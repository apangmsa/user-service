package org.iimsa.userservice.application.service;

import java.util.List;
import lombok.RequiredArgsConstructor;
import org.iimsa.userservice.domain.model.User;
import org.iimsa.userservice.domain.query.UserQueryRepository;
import org.iimsa.userservice.domain.service.hubdeliverymanager.RoundRobinCounter;
import org.iimsa.userservice.presentation.dto.DeliveryManagerSequenceResponse;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class DeliveryManagerService {

    private final UserQueryRepository userQueryRepository;
    private final RoundRobinCounter counter;

    public DeliveryManagerSequenceResponse getNext() {

        List<User> managers = userQueryRepository.findHubDeliveryManagers();

        int next = counter.next();
        int index = next % managers.size();

        User selected = managers.get(index);

        return new DeliveryManagerSequenceResponse(
                selected.getId(),
                selected.getUsername(),
                selected.getSlackId(),
                selected.getDeliveryManager().getSequence()
        );
    }
}
