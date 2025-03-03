package org.projects.ts4.consumer.classes;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import okhttp3.Response;
import org.projects.ts4.avro.WebsiteModel;
import org.projects.ts4.consumer.utlities.WebsiteUtility;
import org.projects.ts4.utility.enums.ResponseEnum;
import org.projects.ts4.utility.enums.ServiceEnum;

import static org.projects.ts4.utility.constants.StringConstants.EMPTY;

@Slf4j
@AllArgsConstructor
public class WebsiteLogger {
    
    public final WebsiteUtility websiteUtility;
    public final WebsiteModel websiteModel;

    public void print(ResponseEnum responseEnum) {
        if (responseEnum == ResponseEnum.DOWNLOAD && websiteModel.getFilename().equals(EMPTY)) {
            log.error("changing response from download to error");
            responseEnum = ResponseEnum.ERROR;
        }

        String string = String.format("%-15s%-40s%s", responseEnum, websiteModel.getUuid(), websiteModel);

        switch (responseEnum) {
            case DOWNLOAD -> send(websiteModel, ServiceEnum.DOWNLOADER);
            case ERROR, UNKNOWN, FAILURE, INVALID -> {
                websiteUtility.write(responseEnum, websiteModel);
                log.error(string);
            }
            default -> log.info(string);
        }
    }

    public WebsiteLogger create(WebsiteModel model) {
        return new WebsiteLogger(websiteUtility, model);
    }

    public void send(WebsiteModel singleton, ServiceEnum serviceEnum) {
        websiteUtility.send(singleton, serviceEnum);
    }

    public Response getResponse() {
        return websiteUtility.getResponse(websiteModel);
    }

    public String getContent() {
        return websiteUtility.getContent(websiteModel);
    }

    public void exception(Exception e) {
        log.error("exception thrown for {}: {}", websiteModel.getUrl(), e.getMessage());
        print(ResponseEnum.ERROR);
    }

}
