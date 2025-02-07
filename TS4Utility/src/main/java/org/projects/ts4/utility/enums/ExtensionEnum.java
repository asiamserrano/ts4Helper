package org.projects.ts4.utility.enums;

import lombok.AllArgsConstructor;
import okhttp3.Response;
import java.io.File;

import static org.projects.ts4.utility.constants.StringConstants.EMPTY;

@AllArgsConstructor
public enum ExtensionEnum {

    ZIP("zip", ".zip"),
    PACKAGE("octet-stream", ".package"),
    RAR("x-rar-compressed", ".rar");

    public final String type;
    public final String extension;

    public static String getExtension(ExtensionEnum extensionEnum) {
        return extensionEnum == null ? EMPTY : extensionEnum.extension;
    }

    public static ExtensionEnum valueOf(Response response) {
        String contentType = response.header("Content-Type");
        response.close();
        return valueOf(contentType, ValidationEnum.CONTAINS);
    }

    public static ExtensionEnum valueOf(File file) {
        String string = file.getName();
        return valueOf(string, ValidationEnum.ENDS_WITH);
    }

    private static ExtensionEnum valueOf(String string, ValidationEnum validationEnum) {
        for (ExtensionEnum extensionEnum : ExtensionEnum.values())
            if (validationEnum.validate(string, extensionEnum)) return extensionEnum;
        return null;
    }

    private enum ValidationEnum {
        ENDS_WITH, CONTAINS;

        boolean validate(String string, ExtensionEnum extensionEnum) {
            return switch (this) {
                case ENDS_WITH -> string.endsWith(extensionEnum.extension);
                case CONTAINS -> string.contains(extensionEnum.type);
            };
        }

    }

}
