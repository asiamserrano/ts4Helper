package ts4.helper.TS4Downloader.enums;

import lombok.AllArgsConstructor;
import okhttp3.Response;

import java.io.File;

@AllArgsConstructor
public enum ExtensionEnum {
    ZIP("zip", ".zip"),
    PACKAGE("octet-stream", ".package");

    public final String type;
    public final String extension;

//    public static ExtensionEnum get(Response response) {
//        String contentType = response.header("Content-Type");
//        if (contentType != null) {
//            for (ExtensionEnum extensionEnum : ExtensionEnum.values()) {
//                if (contentType.contains(extensionEnum.type)) {
//                    return extensionEnum;
//                }
//            }
//        }
//        return null;
//    }

    public static String getExtension(Response response) {
        String contentType = response.header("Content-Type");
        if (contentType != null) {
            for (ExtensionEnum extensionEnum : ExtensionEnum.values()) {
                if (contentType.contains(extensionEnum.type)) {
                    return extensionEnum.extension;
                }
            }
        }
        return ".unknown";
    }

    public static boolean isZipExtension(File file) {
        return file.toString().contains(ZIP.extension);
    }

}
