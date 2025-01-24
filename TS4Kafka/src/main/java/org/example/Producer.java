package org.example;

import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.clients.producer.RecordMetadata;
import org.apache.kafka.common.serialization.BytesSerializer;
import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.core.ProducerFactory;
import org.springframework.kafka.support.SendResult;

import com.google.common.io.Resources;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;
import java.util.concurrent.CompletableFuture;

public class Producer {

    public static final String BOOTSTRAP_SERVERS = "localhost:9092";

    public static void main(String[] args) throws Exception{

//        URLModel urlModel = new URLModel(new URL("https://www.google.com/"), "google");
//        sendMessage(urlModel.toJSON().toJSONString(), "my-second-topic");

        URL contentURL = Resources.getResource("input.txt");
        Files.readAllLines(Paths.get(contentURL.toURI()), StandardCharsets.UTF_8).parallelStream()
                .map(str -> {
                    try {
                        return new WebsiteModel(str);
                    } catch (Exception e) {
                        System.out.println("unable to create website model " + e.getMessage());
                        throw new RuntimeException(e);
                    }
                })
                .forEach(str -> sendMessage(str, "my-topic"));
    }

    public static void sendMessage(WebsiteModel websiteModel, String topic) {
        String message = websiteModel.toString();
        try {
            CompletableFuture<SendResult<String, String>> future = kafkaTemplate().send(topic, message);
            SendResult<String, String> sendResult = future.get();
            RecordMetadata recordMetadata = sendResult.getRecordMetadata();
            System.out.println("Sent message=[" + message +
                    "] with offset=[" + recordMetadata.offset() + "]"
                    + " and partition=[" + recordMetadata.partition() + "]");
        } catch (Exception ex) {
            System.out.println("Unable to send message=[" +
                    message + "] due to : " + ex.getMessage());
        }
    }

    public static KafkaTemplate<String, String> kafkaTemplate() {
        Map<String, Object> configProps = new HashMap<>();
        configProps.put(
                ProducerConfig.BOOTSTRAP_SERVERS_CONFIG,
                BOOTSTRAP_SERVERS);
        configProps.put(
                ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG,
                StringSerializer.class);
        configProps.put(
                ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG,
                StringSerializer.class);
        ProducerFactory<String, String> producerFactory = new DefaultKafkaProducerFactory<>(configProps);
        return new KafkaTemplate<>(producerFactory);
    }

}
