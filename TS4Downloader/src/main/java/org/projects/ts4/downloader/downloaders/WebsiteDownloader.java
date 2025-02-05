package org.projects.ts4.downloader.downloaders;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;
import net.lingala.zip4j.ZipFile;
import org.projects.ts4.avro.WebsiteModel;
import org.projects.ts4.utility.enums.ExtensionEnum;
import org.projects.ts4.utility.utilities.URLUtility;
import org.springframework.context.annotation.Profile;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Service;

import java.io.File;
import java.net.URL;
import java.util.Arrays;
import java.util.Objects;
import java.util.concurrent.ExecutorService;

import static org.projects.ts4.utility.constants.ConfigConstants.PROFILE;

@Slf4j
@Service
@Profile(PROFILE)
@AllArgsConstructor
public class WebsiteDownloader {

    private final ExecutorService executorService;

    @KafkaListener(topics = "${spring.kafka.template.ts4.downloader.topic}", groupId = "${spring.kafka.consumer.group-id}")
    public void listenGroupFoo(@Payload WebsiteModel model, Acknowledgment acknowledgment) {
        log.info("received payload from kafka: {}", model);
        acknowledgment.acknowledge();
        executorService.execute(() -> {
            log.info("received payload from kafka: {}", model);
            File directory = new File(model.getDirectory());
            String filename = model.getFilename();
            if (Arrays.asList(Objects.requireNonNull(directory.list())).contains(filename)) {
                log.info("{} has already been downloaded", filename);
            } else {
                File destination = new File(directory, filename);
                URL source = URLUtility.createURL(model);
                download(destination, source);
            }
        });
    }

    private void download(File destination, URL source) {
        try {
            FileUtils.copyURLToFile(source, destination);
            log.info("downloaded url {} to {}", source, destination);
            if (ExtensionEnum.valueOf(destination) == ExtensionEnum.ZIP) {
                log.info("Unzipping file {}", destination);
                unzip(destination);
            }
        } catch (Exception e) {
            log.error("unable to download url {} to {}", source, destination, e);
        }
    }

    private void unzip(File destination) {
        try (ZipFile zipFile = new ZipFile(destination)) {
            zipFile.extractAll(destination.getParent());
            if (destination.delete()) {
                log.info("file deleted: {}", destination);
            } else {
                log.error("unable to delete file: {}", destination);
            }
        } catch (Exception e) {
            log.error("unable to unzip file {}: {}", destination, e.getMessage());
        }
    }

}