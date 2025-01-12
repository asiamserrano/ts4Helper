package ts4.helper.TS4Downloader.enums;

import lombok.AllArgsConstructor;
import okhttp3.Response;

@AllArgsConstructor
public enum ExtensionEnum {
    ZIP("zip", ".zip"),
    PACKAGE("octet-stream", ".package");

    public final String type;
    public final String extension;

    public static ExtensionEnum get(Response response) {
        String contentType = response.header("Content-Type");
        if (contentType != null) {
            for (ExtensionEnum extensionEnum : ExtensionEnum.values()) {
                if (contentType.contains(extensionEnum.type)) {
                    return extensionEnum;
                }
            }
        }
        return null;
    }

}
