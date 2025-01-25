package org.example.configuration;

import org.example.ts4package.classes.TS4ExecutorService;
import org.example.ts4package.utilities.KafkaUtility;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import org.springframework.context.annotation.Profile;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.KafkaTemplate;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static org.example.ts4package.constants.ConfigConstants.PROFILE;

@EnableKafka
@Configuration
@Profile(PROFILE)
public class KafkaConfig {

    @Value("${spring.kafka.bootstrap-servers}")
    private String bootstrapServers;

    @Value("${spring.kafka.consumer.group-id}")
    private String groupId;

    @Bean
    public KafkaTemplate<String, String> kafkaTemplate() {
        return KafkaUtility.createKafkaTemplate(bootstrapServers);
    }

    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, String> kafkaListenerContainerFactory() {
        return KafkaUtility.createKafkaListenerContainerFactory(bootstrapServers, groupId);
    }

    @Bean
    public TS4ExecutorService ts4ExecutorService() {
        return new TS4ExecutorService(100, TS4ExecutorService.Action.DOWNLOAD);
    }

}
