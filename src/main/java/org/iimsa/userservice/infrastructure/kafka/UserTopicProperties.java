package org.iimsa.userservice.infrastructure.kafka;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "topics.user")
public record UserTopicProperties(
        String approved,
        String deleted,
        String updated
) {
}
