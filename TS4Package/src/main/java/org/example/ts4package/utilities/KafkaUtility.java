package org.example.ts4package.utilities;

import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.clients.producer.RecordMetadata;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.apache.kafka.common.serialization.StringSerializer;
import org.example.ts4package.enums.TopicEnum;
import org.example.ts4package.models.MessageModel;
import org.example.ts4package.models.WebsiteModel;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.core.ProducerFactory;
import org.springframework.kafka.support.SendResult;

import java.io.File;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

@Slf4j
public abstract class KafkaUtility {

    public static KafkaTemplate<String, String> createKafkaTemplate(String bootstrapServers) {
        Map<String, Object> configProps = new HashMap<>();
        configProps.put(
                ProducerConfig.BOOTSTRAP_SERVERS_CONFIG,
                bootstrapServers);
        configProps.put(
                ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG,
                StringSerializer.class);
        configProps.put(
                ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG,
                StringSerializer.class);
        ProducerFactory<String, String> producerFactory = new DefaultKafkaProducerFactory<>(configProps);
        return new KafkaTemplate<>(producerFactory);
    }

    public static ConcurrentKafkaListenerContainerFactory<String, String> createKafkaListenerContainerFactory
            (String bootstrapServers, String consumerGroup) {
        Map<String, Object> props = new HashMap<>();
        props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
        props.put(ConsumerConfig.GROUP_ID_CONFIG, consumerGroup);
        props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        ConsumerFactory<String, String> consumerFactory = new DefaultKafkaConsumerFactory<>(props);
        ConcurrentKafkaListenerContainerFactory<String, String> f = new ConcurrentKafkaListenerContainerFactory<>();
        f.setConsumerFactory(consumerFactory);
        return f;
    }

    public static void send(File file, TopicEnum topic, KafkaTemplate<String, String> template) {
        String message = file.getAbsolutePath();
        send(message, topic, template);
    }

    public static void send(WebsiteModel model, TopicEnum topic, KafkaTemplate<String, String> template) {
        String message = model.toString();
        send(message, topic, template);
    }

    public static void send(MessageModel model, TopicEnum topic, KafkaTemplate<String, String> template) {
        String message = model.toString();
        send(message, topic, template);
    }

    private static void send(String message, TopicEnum topicEnum, KafkaTemplate<String, String> kafkaTemplate) {
        try {
            CompletableFuture<SendResult<String, String>> future = kafkaTemplate
                    .send(topicEnum.topic, message);
            SendResult<String, String> sendResult = future.get();
            RecordMetadata recordMetadata = sendResult.getRecordMetadata();
            log.info("Sent message=[{}] to topic {}", message, recordMetadata.topic());
        } catch (Exception ex) {
            log.error("unable to send message=[{}] due to exception", message, ex);
        }
    }

}
