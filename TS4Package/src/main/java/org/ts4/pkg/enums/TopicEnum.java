package org.ts4.pkg.enums;

import lombok.AllArgsConstructor;

@AllArgsConstructor
public enum TopicEnum {
    MY_TOPIC("my-topic"),
    MY_SECOND_TOPIC("my-second-topic"),
    MY_THIRD_TOPIC("my-third-topic"),;

    public final String topic;
}
