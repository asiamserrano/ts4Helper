package org.projects.ts4.consumer.utlities;

import lombok.extern.slf4j.Slf4j;
import okhttp3.OkHttpClient;
import okhttp3.Response;
import org.apache.kafka.clients.producer.RecordMetadata;
import org.projects.ts4.avro.WebsiteModel;
import org.projects.ts4.consumer.classes.WebsiteLogger;
import org.projects.ts4.consumer.enums.BaseEnumImpl;
import org.projects.ts4.utility.classes.KafkaTopics;
import org.projects.ts4.utility.classes.ResponseFiles;
import org.projects.ts4.utility.classes.Scheduler;
import org.projects.ts4.utility.enums.ServiceEnum;
import org.projects.ts4.utility.enums.ResponseEnum;
import org.projects.ts4.utility.utilities.OkHttpUtility;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.kafka.support.SendResult;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.io.File;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;
import java.util.concurrent.ThreadPoolExecutor;

import static org.projects.ts4.utility.constants.ConfigConstants.PROFILE;
import static org.projects.ts4.utility.constants.StringConstants.EMPTY;

@Slf4j
@Component
@Profile(PROFILE)
@EnableScheduling
//@AllArgsConstructor
public class WebsiteUtility extends Scheduler {

    private final KafkaTemplate<String, WebsiteModel> kafkaTemplate;
    private final KafkaTopics kafkaTopics;
    public final OkHttpClient okHttpClient;
    public final File directoryFile;
    private final ResponseFiles responseFiles;

    @Autowired
    public WebsiteUtility(final KafkaTemplate<String, WebsiteModel> kafkaTemplate,
                          final KafkaTopics kafkaTopics,
                          final OkHttpClient okHttpClient,
                          final File directoryFile,
                          final ResponseFiles responseFiles,
                          final ExecutorService executorService) {
        super(executorService);
        this.kafkaTemplate = kafkaTemplate;
        this.kafkaTopics = kafkaTopics;
        this.okHttpClient = okHttpClient;
        this.directoryFile = directoryFile;
        this.responseFiles = responseFiles;
    }

    public File createDirectory(ZonedDateTime date) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd_HH.mm.ss");
        String child = "download_" + date.format(formatter);
        File file = new File(directoryFile, child);
        if (file.mkdir()) {
            return file;
        } else {
            String path = file.getAbsolutePath();
            log.error("Unable to create directory {}", path);
            throw new RuntimeException(path);
        }
    }

    public void consume(WebsiteModel websiteModel) {
        executorService.execute(() -> {
            log.info("consuming {}", websiteModel);
            BaseEnumImpl baseEnumImpl = BaseEnumImpl.valueOf(websiteModel);
            WebsiteLogger websiteLogger = new WebsiteLogger(this, websiteModel);
            if (baseEnumImpl == null) {
                websiteLogger.print(ResponseEnum.UNKNOWN);
            } else {
                baseEnumImpl.parse(websiteLogger);
            }
        });
    }

    public void send(WebsiteModel websiteModel, ServiceEnum topicEnum) {
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

    public void write(ResponseEnum responseEnum, WebsiteModel websiteModel) {
        executorService.execute(() -> responseFiles.write(responseEnum, websiteModel));
    }

    public Response getResponse(WebsiteModel websiteModel) {
        return OkHttpUtility.sendRequest(websiteModel, okHttpClient);
    }

    public String getContent(WebsiteModel websiteModel) {
        return OkHttpUtility.getContent(websiteModel, okHttpClient);
    }

}
