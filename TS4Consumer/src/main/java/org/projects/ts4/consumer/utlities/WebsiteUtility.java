package org.projects.ts4.consumer.utlities;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.projects.ts4.avro.WebsiteModel;
import org.projects.ts4.consumer.enums.BaseEnumImpl;
import org.projects.ts4.consumer.producers.WebsiteProducer;
import org.projects.ts4.utility.enums.KafkaTopicEnum;
import org.projects.ts4.utility.enums.ResponseEnum;
import org.springframework.context.annotation.Profile;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.io.File;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.concurrent.ExecutorService;

import static org.projects.ts4.utility.constants.ConfigConstants.PROFILE;
import static org.projects.ts4.utility.constants.StringConstants.EMPTY;

@Slf4j
@Component
@Profile(PROFILE)
@AllArgsConstructor
public class WebsiteUtility {

//    public static void main(String[] args) {
//        WebsiteModel websiteModel = new WebsiteModel();
//        websiteModel.setDirectory("/src/files");
//        websiteModel.setFilename("parent_file.txt");
//        websiteModel.setUrl("https://www.google.com");
//
//        WebsiteModel websiteModel2 = new WebsiteModel();
//        websiteModel2.setFilename("child_file.txt");
//        websiteModel2.setUrl("https://www.google.com");
//        websiteModel2.setPrevious(websiteModel);
//
//        WebsiteModel websiteModel3 = new WebsiteModel();
//        websiteModel3.setFilename("child_2_file.txt");
//        websiteModel3.setUrl("https://www.google.com");
//        websiteModel3.setPrevious(websiteModel2);
//
//        WebsiteModel fixed = WebsiteUtility.setDirectory(websiteModel3);
//
//        if (fixed == null) {
//            System.out.println("problem");
//        } else {
//            System.out.println(fixed);
//        }
//
//    }

    private final WebsiteProducer websiteProducer;
    private final ExecutorService executorService;

    public String createDirectory(ZonedDateTime date) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd_HH.mm.ss");
        String child = "download_" + date.format(formatter);
        File file = new File(websiteProducer.directoryFile, child);
        String path = file.getAbsolutePath();

        if (file.mkdir()) {
            return path;
        } else {
            log.error("Unable to create directory {}", path);
            throw new RuntimeException(path);
        }
    }

    public void consume(WebsiteModel websiteModel) {
        executorService.execute(() -> {
            log.info("consuming {}", websiteModel);
            BaseEnumImpl baseEnumImpl = BaseEnumImpl.valueOf(websiteModel);
            if (baseEnumImpl == null) {
                WebsiteUtility.print(websiteModel, ResponseEnum.UNKNOWN, websiteProducer);
            } else {
                baseEnumImpl.parse(websiteModel, websiteProducer);
            }
        });
    }

    public static void print(WebsiteModel websiteModel, ResponseEnum responseEnum, WebsiteProducer websiteProducer) {
        if (responseEnum == ResponseEnum.DOWNLOAD && websiteModel.getFilename().equals(EMPTY)) {
            log.error("changing response from download to error");
            responseEnum = ResponseEnum.ERROR;
        }

        String string = String.format("%-15s%s", responseEnum, websiteModel.toString());

        switch (responseEnum) {
            case DOWNLOAD -> websiteProducer.send(websiteModel, KafkaTopicEnum.DOWNLOADER);
            case ERROR, UNKNOWN, FAILURE -> log.error(string);
            default -> log.info(string);
        }
    }

}
