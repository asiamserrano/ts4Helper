package org.projects.ts4.utility.enums;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.projects.ts4.avro.WebsiteModel;

@AllArgsConstructor

@Slf4j
public enum ResponseEnum {
    SUCCESSFUL, FAILURE, UNKNOWN, DOWNLOAD, COMPLETE, DOWNLOADED, ERROR;

    public static ResponseEnum getResponseEnum(WebsiteModel websiteModel, ExtensionEnum extensionEnum) {
        if (extensionEnum == null) {
            log.error("unhandled content type for {}", websiteModel);
            return ResponseEnum.FAILURE;
        } else {
            return ResponseEnum.DOWNLOAD;
        }
    }

}