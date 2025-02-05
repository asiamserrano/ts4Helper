package org.projects.ts4.consumer.producers;

import lombok.AllArgsConstructor;
import okhttp3.OkHttpClient;
import org.apache.kafka.clients.producer.RecordMetadata;
import org.projects.ts4.utility.classes.KafkaTopics;
import org.projects.ts4.utility.enums.KafkaTopicEnum;
import lombok.extern.slf4j.Slf4j;
import org.projects.ts4.avro.WebsiteModel;
import org.springframework.context.annotation.Profile;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Service;

import java.io.File;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;

import static org.projects.ts4.utility.constants.ConfigConstants.PROFILE;

@Service
@Slf4j
@Profile(PROFILE)
@AllArgsConstructor
public class WebsiteProducer {

    public final OkHttpClient okHttpClient;
    public final File directoryFile;
    private final KafkaTemplate<String, WebsiteModel> kafkaTemplate;
    private final KafkaTopics kafkaTopics;
    private final ExecutorService executorService;

    public void send(WebsiteModel websiteModel, KafkaTopicEnum topicEnum) {
        executorService.execute(() -> {
            try {
                String topic = kafkaTopics.get(topicEnum);
                CompletableFuture<SendResult<String, WebsiteModel>> future = kafkaTemplate
                        .send(topic, websiteModel);
                SendResult<String, WebsiteModel> sendResult = future.get();
                RecordMetadata recordMetadata = sendResult.getRecordMetadata();
                log.info("Sent message=[{}] to topic {}", websiteModel, recordMetadata.topic());
            } catch (Exception ex) {
                log.error("unable to send message=[{}] due to exception", websiteModel, ex);
            }
        });
    }

}
