package ts4.helper.TS4Downloader.utilities;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.producer.RecordMetadata;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import ts4.helper.TS4Downloader.enums.TopicEnum;
import ts4.helper.TS4Downloader.models.WebsiteModel;

import java.io.File;
import java.util.concurrent.CompletableFuture;

@Slf4j
public abstract class TopicUtility {

    public static void send(File file, TopicEnum topic, KafkaTemplate<String, String> template) {
        String message = file.getAbsolutePath();
        send(message, topic, template);
    }

    public static void send(WebsiteModel model, TopicEnum topic, KafkaTemplate<String, String> template) {
        String message = model.toString();
        send(message, topic, template);
    }

    private static void send(String message, TopicEnum topicEnum, KafkaTemplate<String, String> kafkaTemplate) {
        try {
            CompletableFuture<SendResult<String, String>> future = kafkaTemplate
                    .send(topicEnum.topic, message);
            SendResult<String, String> sendResult = future.get();
            RecordMetadata recordMetadata = sendResult.getRecordMetadata();
            long offset = recordMetadata.offset();
            int partition = recordMetadata.partition();
            log.info("Sent message=[{}] with offset=[{}] and partition=[{}]", message, offset, partition);
        } catch (Exception ex) {
            log.error("unable to send message=[{}] due to exception", message, ex);
        }
    }

}
