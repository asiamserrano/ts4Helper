package ts4.helper.TS4Downloader.models;

import ts4.helper.TS4Downloader.enums.ResponseEnum;

import static ts4.helper.TS4Downloader.enums.ResponseEnum.UNKNOWN;
import static ts4.helper.TS4Downloader.enums.ResponseEnum.SUCCESSFUL;
import static ts4.helper.TS4Downloader.enums.ResponseEnum.FAILURE;

import java.net.URL;

public class DownloadResponse {

    public final ResponseEnum responseEnum;
    public final URL url;

    public DownloadResponse(boolean bool, URL url) {
        this(bool ? SUCCESSFUL : FAILURE, url);
    }

    public DownloadResponse(URL url) {
        this(UNKNOWN, url);
    }

    private DownloadResponse(ResponseEnum response, URL url) {
        this.url = url;
        this.responseEnum = response;
    }

    @Override
    public String toString() {
        return String.format("%-15s: %s", responseEnum.toString(), url.toString());
    }

}
