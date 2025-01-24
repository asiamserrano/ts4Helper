package ts4.helper.TS4Downloader.consumers;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.lingala.zip4j.ZipFile;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;
import ts4.helper.TS4Downloader.enums.ExtensionEnum;
import ts4.helper.TS4Downloader.utilities.FileUtility;

import java.io.File;
import java.util.concurrent.ExecutorService;

@Service
@Slf4j
@AllArgsConstructor
public class MyThirdTopicConsumer {

    private final ExecutorService unzipper;

    @KafkaListener(topics = "my-third-topic", groupId = "consumer-group")
    public void listenGroupFoo(String message) {
        unzipper.execute(() -> {
            log.info("received message: {}", message);
            try {
                File file = new File(message);
                if (ExtensionEnum.isZipExtension(file)) {
                    log.info("Unzipping file {}", file);
                    ZipFile zipFile = new ZipFile(file);
                    zipFile.extractAll(file.getParent());
                    FileUtility.deleteFile(file);
                } else {
                    log.info("{}} is not a zip file", file);
                }
            } catch (Exception e) {
                log.error("unable to unzip file {}: {}", message, e.getMessage());
            }
        });
    }

}