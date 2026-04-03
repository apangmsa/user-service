package org.iimsa.userservice.domain.event;

import org.iimsa.userservice.domain.model.User;

public interface UserUpdatedEvent {
    void updated(User user);
}
