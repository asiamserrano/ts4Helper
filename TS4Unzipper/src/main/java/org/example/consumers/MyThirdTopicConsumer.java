package org.example.consumers;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.lingala.zip4j.ZipFile;
import org.example.ts4package.classes.TS4ExecutorService;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;
import org.example.ts4package.enums.ExtensionEnum;

import java.io.File;
import java.util.concurrent.ExecutorService;

@Service
@Slf4j
@AllArgsConstructor
public class MyThirdTopicConsumer {

    private TS4ExecutorService ts4ExecutorService;

    @KafkaListener(topics = "${spring.kafka.template.default-topic}", groupId = "${spring.kafka.consumer.group-id}")
    public void listenGroupFoo(String message) {
        ts4ExecutorService.executorService.execute(() -> {
            log.info("received message: {}", message);
            try {
                File file = new File(message);
                if (ExtensionEnum.isZipExtension(file)) {
                    log.info("Unzipping file {}", file);
                    ZipFile zipFile = new ZipFile(file);
                    zipFile.extractAll(file.getParent());
                    if (file.delete()) {
                        log.info("file deleted: {}", file);
                    } else {
                        log.error("unable to delete file: {}", file);
                    }
                } else {
                    log.info("{}} is not a zip file", file);
                }
            } catch (Exception e) {
                log.error("unable to unzip file {}: {}", message, e.getMessage());
            }
        });
    }

}