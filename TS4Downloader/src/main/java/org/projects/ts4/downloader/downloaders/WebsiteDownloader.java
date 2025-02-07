package org.projects.ts4.downloader.downloaders;

import lombok.extern.slf4j.Slf4j;
import org.projects.ts4.avro.WebsiteModel;
import org.projects.ts4.downloader.classes.WebsiteLogger;
import org.projects.ts4.utility.classes.ResponseFiles;
import org.projects.ts4.utility.classes.Scheduler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Service;

import java.io.File;
import java.util.Arrays;
import java.util.Objects;
import java.util.concurrent.ExecutorService;

import static org.projects.ts4.utility.constants.ConfigConstants.PROFILE;

@Slf4j
@Service
@Profile(PROFILE)
public class WebsiteDownloader extends Scheduler {

    private final File tarShellScript;
    private final ResponseFiles responseFiles;

    @Autowired
    public WebsiteDownloader(final File tarShellScript, final ResponseFiles responseFiles,
                             final ExecutorService executorService) {
        super(executorService);
        this.tarShellScript = tarShellScript;
        this.responseFiles = responseFiles;
    }

    @KafkaListener(topics = "${spring.kafka.template.ts4.downloader.topic}", groupId = "${spring.kafka.consumer.group-id}")
    public void listenGroupFoo(@Payload WebsiteModel model, Acknowledgment acknowledgment) {
        executorService.execute(() -> {
            log.info("received payload from kafka: {}", model);
            acknowledgment.acknowledge();
            File directory = new File(model.getDirectory());
            String filename = model.getFilename();
            if (Arrays.asList(Objects.requireNonNull(directory.list())).contains(filename)) {
                log.info("{} has already been downloaded", filename);
            } else {
                WebsiteLogger websiteLogger = new WebsiteLogger(model, responseFiles);
                websiteLogger.download(tarShellScript);
            }
        });
    }

}