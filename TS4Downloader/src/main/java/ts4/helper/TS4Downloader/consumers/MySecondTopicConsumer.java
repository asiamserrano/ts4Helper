package ts4.helper.TS4Downloader.consumers;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.messaging.Message;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.stereotype.Service;
import ts4.helper.TS4Downloader.enums.ExtensionEnum;
import ts4.helper.TS4Downloader.enums.TopicEnum;
import ts4.helper.TS4Downloader.models.URLModel;
import ts4.helper.TS4Downloader.threads.URLModelThread;
import ts4.helper.TS4Downloader.utilities.TopicUtility;
import ts4.helper.TS4Downloader.utilities.UnzipUtility;

import java.io.File;
import java.io.FileOutputStream;
import java.net.URL;
import java.net.URLDecoder;
import java.nio.channels.Channels;
import java.nio.channels.FileChannel;
import java.nio.channels.ReadableByteChannel;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.ExecutorService;

import static ts4.helper.TS4Downloader.constants.StringConstants.BACK_SLASHES;
import static ts4.helper.TS4Downloader.constants.StringConstants.EMPTY;

@Service
@Slf4j
@AllArgsConstructor
public class MySecondTopicConsumer {

    private final ExecutorService downloader;
    private KafkaTemplate<String, String> kafkaTemplate;

    private static File directory = new File("/Users/asiaserrano/ChromeDownloads");

    @KafkaListener(topics = "my-second-topic", groupId = "consumer-group")
    public void listenGroupFoo(String message) {
        downloader.execute(() -> {
            log.info("received message: {}", message);
            JSONObject msg = (JSONObject) JSONValue.parse(message);
            URLModel model = new URLModel(msg);
            File destination = new File(directory, model.name);
            URL source = model.url;
            try (FileOutputStream fileOutputStream = new FileOutputStream(destination)) {
                ReadableByteChannel readableByteChannel = Channels.newChannel(source.openStream());
                FileChannel fileChannel = fileOutputStream.getChannel();
                fileChannel.transferFrom(readableByteChannel, 0, Long.MAX_VALUE);
                log.info("downloaded url {} to {}", source, destination);
                TopicUtility.send(destination, TopicEnum.MY_THIRD_TOPIC, kafkaTemplate);
            } catch (Exception e) {
                log.error("unable to download url {} to {}", source, destination, e);
            }
        });
    }

}
