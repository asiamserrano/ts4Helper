package org.projects.ts4.consumer.consumers;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.projects.ts4.avro.WebsiteModel;
import org.projects.ts4.consumer.utlities.WebsiteUtility;
import org.springframework.context.annotation.Profile;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Service;

import java.util.concurrent.ExecutorService;

import static org.projects.ts4.utility.constants.ConfigConstants.PROFILE;

@Service
@Slf4j
@AllArgsConstructor
@Profile(PROFILE)
public class WebsiteConsumer {

    private final WebsiteUtility websiteUtility;
    private final ExecutorService executorService;

    @KafkaListener(topics = "${spring.kafka.template.ts4.consumer.topic}", groupId = "${spring.kafka.consumer.group-id}")
    public void listenGroupFoo(@Payload WebsiteModel model, Acknowledgment acknowledgment) {
        executorService.execute(() -> {
            log.info("received payload from kafka: {}", model);
            websiteUtility.consume(model);
            acknowledgment.acknowledge();
        });
    }

}