package org.projects.ts4.utility.classes;

import lombok.extern.slf4j.Slf4j;
import org.projects.ts4.utility.enums.KafkaTopicEnum;

import java.util.HashMap;
import java.util.Map;
@Slf4j

public class KafkaTopics {

    private final Map<KafkaTopicEnum, String> topics;

    private KafkaTopics(Map<KafkaTopicEnum, String> topics) {
        this.topics = topics;
    }

    public String get(KafkaTopicEnum topic) {
        return topics.get(topic);
    }

    public static class Builder {

        private final Map<KafkaTopicEnum, String> topics;

        public Builder() {
            this.topics = new HashMap<>();
        }

        public Builder add(KafkaTopicEnum topic, String value) {
            this.topics.put(topic, value);
            return this;
        }

        public KafkaTopics build() {
            return new KafkaTopics(topics);
        }

    }

}
