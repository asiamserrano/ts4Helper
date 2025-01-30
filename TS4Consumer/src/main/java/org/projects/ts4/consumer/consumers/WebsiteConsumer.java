package org.projects.ts4.consumer.consumers;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.projects.ts4.avro.WebsiteModel;
import org.projects.ts4.consumer.utlities.WebsiteUtility;
import org.springframework.context.annotation.Profile;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static org.projects.ts4.utility.constants.ConfigConstants.PROFILE;

@Service
@Slf4j
@AllArgsConstructor
@Profile(PROFILE)
public class WebsiteConsumer {

    private final ExecutorService executorService = Executors.newFixedThreadPool(2);

    @KafkaListener(topics = "${spring.kafka.template.default-topic}", groupId = "${spring.kafka.consumer.group-id}")
    public void listenGroupFoo(WebsiteModel model) {
        log.info("received payload: {}", model);
//        executorService.execute(() -> {
//            WebsiteUtility.consume(model);
//        });
    }

}