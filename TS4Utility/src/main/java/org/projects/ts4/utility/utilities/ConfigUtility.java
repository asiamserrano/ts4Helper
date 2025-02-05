package org.projects.ts4.utility.utilities;

import io.confluent.kafka.serializers.KafkaAvroDeserializer;
import io.confluent.kafka.serializers.KafkaAvroSerializer;
import io.confluent.kafka.serializers.KafkaAvroDeserializerConfig;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.listener.ContainerProperties;

import java.util.HashMap;

import static org.projects.ts4.utility.constants.ConfigConstants.SCHEMA_REGISTRY_URL_HEADER;
import static org.projects.ts4.utility.constants.ConfigConstants.SCHEMA_REGISTRY_URL_VALUE;
import static org.projects.ts4.utility.constants.ConfigConstants.AUTO_OFFSET_RESET_VALUE;

@Slf4j
public abstract class ConfigUtility {

    public static ConcurrentKafkaListenerContainerFactory<String, ?> concurrentKafkaListenerContainerFactory
            (String bootstrapServers, String groupId) {
        ConcurrentKafkaListenerContainerFactory<String, ?> concurrentKafkaListenerContainerFactory;
        concurrentKafkaListenerContainerFactory = new ConcurrentKafkaListenerContainerFactory<>();
        concurrentKafkaListenerContainerFactory.setConsumerFactory(new DefaultKafkaConsumerFactory<>(new HashMap<>() {{
            put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
            put(ConsumerConfig.GROUP_ID_CONFIG, groupId);
            put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
            put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, KafkaAvroDeserializer.class);
            put(SCHEMA_REGISTRY_URL_HEADER, SCHEMA_REGISTRY_URL_VALUE);
            put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, AUTO_OFFSET_RESET_VALUE);
            put(KafkaAvroDeserializerConfig.SPECIFIC_AVRO_READER_CONFIG, true);
            put(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, false);

        }}));
        concurrentKafkaListenerContainerFactory.getContainerProperties()
                .setAckMode(ContainerProperties.AckMode.MANUAL_IMMEDIATE);
        return concurrentKafkaListenerContainerFactory;
    }

    public static KafkaTemplate<String, ?> createKafkaTemplate(String bootstrapServers) {
        return new KafkaTemplate<>(new DefaultKafkaProducerFactory<>(new HashMap<>() {{
            put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
            put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
            put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, KafkaAvroSerializer.class);
            put(SCHEMA_REGISTRY_URL_HEADER, SCHEMA_REGISTRY_URL_VALUE);
        }}));
    }

}
