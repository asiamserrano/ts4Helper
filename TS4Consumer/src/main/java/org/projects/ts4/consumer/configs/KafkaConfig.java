package org.projects.ts4.consumer.configs;

import lombok.extern.slf4j.Slf4j;
import org.projects.ts4.utility.classes.KafkaTopics;
import org.projects.ts4.utility.enums.KafkaTopicEnum;
import org.projects.ts4.utility.utilities.ConfigUtility;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.projects.ts4.avro.WebsiteModel;
import org.springframework.kafka.core.KafkaTemplate;

import static org.projects.ts4.utility.constants.ConfigConstants.PROFILE;

import static org.projects.ts4.utility.constants.StringConstants.UNCHECKED;

@EnableKafka
@Configuration
@Profile(PROFILE)
@Slf4j
public class KafkaConfig {

    @Value("${spring.kafka.bootstrap-servers}")
    private String bootstrapServers;

    @Value("${spring.kafka.consumer.group-id}")
    private String groupId;

    @Value("${spring.kafka.template.ts4.downloader.topic}")
    private String ts4DownloaderTopic;

    @Value("${spring.kafka.template.ts4.consumer.topic}")
    private String ts4ConsumerTopic;

    @Bean @SuppressWarnings(UNCHECKED)
    public ConcurrentKafkaListenerContainerFactory<String, WebsiteModel> kafkaListenerContainerFactory() {
        ConcurrentKafkaListenerContainerFactory<String, ?> factory;
        factory = ConfigUtility.concurrentKafkaListenerContainerFactory(bootstrapServers, groupId);
        return (ConcurrentKafkaListenerContainerFactory<String, WebsiteModel>) factory;
    }

    @Bean @SuppressWarnings(UNCHECKED)
    public KafkaTemplate<String, WebsiteModel> kafkaTemplate() {
        KafkaTemplate<String, ?> kafkaTemplate = ConfigUtility.createKafkaTemplate(bootstrapServers);
        return (KafkaTemplate<String, WebsiteModel>) kafkaTemplate;
    }

    @Bean
    public KafkaTopics kafkaTopics() {
        return new KafkaTopics.Builder()
                .add(KafkaTopicEnum.DOWNLOADER, ts4DownloaderTopic)
                .add(KafkaTopicEnum.CONSUMER, ts4ConsumerTopic)
                .build();
    }

}
