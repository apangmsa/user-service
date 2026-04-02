package org.iimsa.userservice.infrastructure.hub;

import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.iimsa.userservice.domain.service.hubdeliverymanager.HubData;
import org.iimsa.userservice.domain.service.hubdeliverymanager.HubProvider;
import org.iimsa.userservice.infrastructure.hub.client.HubClient;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class HubProviderImpl implements HubProvider {

    private final HubClient client;

    @Override
    public HubData get(UUID hubId) {
        // TODO: 허브 서비스 연결
        // HubResponse res = client.getHub(hubId);
        // return res == null || res.id() == null ? null : new HubData(res.name(), res.address());
        return null;
    }
}