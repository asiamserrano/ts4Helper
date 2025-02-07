package org.projects.ts4.utility.classes;

import lombok.extern.slf4j.Slf4j;
import org.projects.ts4.avro.WebsiteModel;
import org.projects.ts4.utility.enums.ResponseEnum;
import org.projects.ts4.utility.enums.ServiceEnum;
import org.projects.ts4.utility.utilities.FileUtility;

import java.io.File;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
public class ResponseFiles {

    public static ResponseFiles build(String directory, ServiceEnum service) {
        File file = FileUtility.createDirectory(directory, "src", "files");
        List<ResponseEnum> responseEnums = ResponseEnum.getResponseEnums(service);
        return new ResponseFiles(responseEnums, file);
    }

    private final Map<ResponseEnum, File> map;

    private ResponseFiles(List<ResponseEnum> responseEnums, File dir) {
        this.map = responseEnums.stream()
                .collect(Collectors.toMap(r -> r, r -> FileUtility.createFile(dir, r)));
    }

    public void write(ResponseEnum responseEnum, WebsiteModel websiteModel) {
        File file = map.get(responseEnum);
        if (file == null) {
            log.error("file not found for response {}: unable to write model {}", responseEnum, websiteModel);
        } else {
            FileUtility.write(file, websiteModel);
        }
    }

}
