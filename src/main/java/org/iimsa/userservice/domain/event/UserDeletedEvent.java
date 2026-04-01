package org.iimsa.userservice.domain.event;

import org.iimsa.userservice.domain.model.User;

public interface UserDeletedEvent {
    void deleted(User user);
}
