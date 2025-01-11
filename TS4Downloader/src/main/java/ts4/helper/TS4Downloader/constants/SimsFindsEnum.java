package ts4.helper.TS4Downloader.constants;

import lombok.Getter;

@Getter
public enum SimsFindsEnum {
    DOWNLOADS("downloads/"),
    CONTINUE("continue?"),
    DOWNLOAD("download?");

    private final String string;

    SimsFindsEnum(String string) {
        this.string = string;
    }

}
