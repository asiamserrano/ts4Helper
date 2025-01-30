package org.projects.ts4.consumer.configuration;

import org.projects.ts4.utility.utilities.ConfigUtility;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.projects.ts4.avro.WebsiteModel;

import static org.projects.ts4.utility.constants.ConfigConstants.PROFILE;

import static org.projects.ts4.utility.constants.StringConstants.UNCHECKED;

@EnableKafka
@Configuration
@Profile(PROFILE)
public class KafkaConfig {

    @Value("${spring.kafka.bootstrap-servers}")
    private String bootstrapServers;

    @Value("${spring.kafka.consumer.group-id}")
    private String groupId;
    
    @Bean @SuppressWarnings(UNCHECKED)
    public ConcurrentKafkaListenerContainerFactory<String, WebsiteModel> kafkaListenerContainerFactory() {
        ConcurrentKafkaListenerContainerFactory<String, ?> factory;
        factory = ConfigUtility.concurrentKafkaListenerContainerFactory(bootstrapServers, groupId);
        return (ConcurrentKafkaListenerContainerFactory<String, WebsiteModel>) factory;
    }

}
