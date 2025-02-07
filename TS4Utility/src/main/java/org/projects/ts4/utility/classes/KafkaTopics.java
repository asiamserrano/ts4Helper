package org.projects.ts4.utility.classes;

import lombok.extern.slf4j.Slf4j;
import org.projects.ts4.utility.enums.ServiceEnum;

import java.util.HashMap;
import java.util.Map;
@Slf4j

public class KafkaTopics {

    private final Map<ServiceEnum, String> topics;

    private KafkaTopics(Map<ServiceEnum, String> topics) {
        this.topics = topics;
    }

    public String get(ServiceEnum topic) {
        return topics.get(topic);
    }

    public static class Builder {

        private final Map<ServiceEnum, String> topics;

        public Builder() {
            this.topics = new HashMap<>();
        }

        public Builder add(ServiceEnum topic, String value) {
            this.topics.put(topic, value);
            return this;
        }

        public KafkaTopics build() {
            return new KafkaTopics(topics);
        }

    }

}
