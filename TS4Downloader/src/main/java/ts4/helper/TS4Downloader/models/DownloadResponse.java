package ts4.helper.TS4Downloader.models;

import lombok.AllArgsConstructor;
import ts4.helper.TS4Downloader.enums.ResponseEnum;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;

@AllArgsConstructor
public class DownloadResponse {

    public final ResponseEnum responseEnum;
    public final List<URL> urls;

    public DownloadResponse(ResponseEnum responseEnum) {
        this.responseEnum = responseEnum;
        this.urls = new ArrayList<>();
    }

}
