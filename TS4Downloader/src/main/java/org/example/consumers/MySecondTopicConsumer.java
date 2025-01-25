package org.example.consumers;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;
import org.example.ts4package.models.MessageModel;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.example.ts4package.enums.TopicEnum;
import org.example.ts4package.models.WebsiteModel;
import org.example.ts4package.utilities.KafkaUtility;

import java.io.File;
import java.net.URL;
import java.util.Arrays;
import java.util.Objects;
import java.util.concurrent.ExecutorService;

@Service
@Slf4j
@AllArgsConstructor
public class MySecondTopicConsumer {

    private final ExecutorService downloader;
    private final KafkaTemplate<String, String> kafkaTemplate;

    @KafkaListener(topics = "${spring.kafka.template.default-topic}", groupId = "${spring.kafka.consumer.group-id}")
    public void listenGroupFoo(String message) {
        downloader.execute(() -> {
            log.info("received message: {}", message);
            MessageModel messageModel = MessageModel.Builder.parse(message).build();
            WebsiteModel websiteModel = messageModel.websiteModel;
            File directory = messageModel.directory;
            String filename = websiteModel.name;
            if (Arrays.asList(Objects.requireNonNull(directory.list())).contains(filename)) {
                log.info("{} has already been downloaded", filename);
            } else {
                File destination = new File(directory, filename);
                URL source = websiteModel.url;
                try {
                    FileUtils.copyURLToFile(source, destination);
                    log.info("downloaded url {} to {}", source, destination);
                    KafkaUtility.send(destination, TopicEnum.MY_THIRD_TOPIC, kafkaTemplate);
                } catch (Exception e) {
                    log.error("unable to download url {} to {}", source, destination, e);
                }
            }
        });
    }

}
