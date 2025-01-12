package ts4.helper.TS4Downloader.models;

import ts4.helper.TS4Downloader.enums.DownloadResponseEnum;

import static ts4.helper.TS4Downloader.enums.DownloadResponseEnum.UNKNOWN;
import static ts4.helper.TS4Downloader.enums.DownloadResponseEnum.SUCCESSFUL;
import static ts4.helper.TS4Downloader.enums.DownloadResponseEnum.FAILURE;

import java.net.URL;

public class DownloadResponse {

    public final DownloadResponseEnum downloadResponseEnum;
    public final URL url;

    public DownloadResponse(boolean bool, URL url) {
        this(bool ? SUCCESSFUL : FAILURE, url);
    }

    public DownloadResponse(URL url) {
        this(UNKNOWN, url);
    }

    private DownloadResponse(DownloadResponseEnum response, URL url) {
        this.url = url;
        this.downloadResponseEnum = response;
    }

    @Override
    public String toString() {
        return String.format("%-15s: %s", downloadResponseEnum.toString(), url.toString());
    }

}
