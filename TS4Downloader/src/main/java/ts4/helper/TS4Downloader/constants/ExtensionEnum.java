package ts4.helper.TS4Downloader.constants;

import lombok.AllArgsConstructor;
import lombok.Getter;
import okhttp3.Response;

@AllArgsConstructor
@Getter
public enum ExtensionEnum {
    ZIP("zip", ".zip"),
    PACKAGE("octet-stream", ".package");

    private final String type;
    private final String extension;

//    public ExtensionEnum(Response response) {
//        String contentType = response.header("Content-Type");
//    }

    public static ExtensionEnum get(Response response) {
        String contentType = response.header("Content-Type");
        if (contentType != null) {
            for (ExtensionEnum extensionEnum : ExtensionEnum.values()) {
                if (contentType.contains(extensionEnum.getType())) {
                    return extensionEnum;
                }
            }
        }
        return null;
    }

}
