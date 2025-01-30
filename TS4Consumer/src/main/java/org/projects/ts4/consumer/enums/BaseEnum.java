package org.projects.ts4.consumer.enums;

import lombok.extern.slf4j.Slf4j;
import org.projects.ts4.avro.WebsiteModel;
import org.projects.ts4.utility.constants.StringConstants;
import org.projects.ts4.utility.constructors.Domain;
import org.projects.ts4.utility.enums.ResponseEnum;

public interface BaseEnum extends Domain {

    void parse(WebsiteModel websiteModel);

    public static void print(WebsiteModel websiteModel, ResponseEnum responseEnum) {
        if (responseEnum == ResponseEnum.DOWNLOAD && websiteModel.getFilename().equals(StringConstants.EMPTY)) {
//            log.info("changing response from download to error");
            responseEnum = ResponseEnum.ERROR;
        }

        String string = String.format("%-15s%s", responseEnum, websiteModel.toString());
        System.out.println(string);
    }

}
