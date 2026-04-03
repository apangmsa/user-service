package org.iimsa.userservice.domain.service.hubdeliverymanager;

import java.util.UUID;

public interface HubProvider {
    HubData get(UUID hubId); // 허브 아이디를 통해 허브 정보 가져옴
}
