package ts4.helper.TS4Downloader.models;

import java.net.URL;

public class DownloadResponse {

    public final String result;

    public DownloadResponse(boolean bool, URL url) {
        this(bool ? "SUCCESSFUL" : "FAILURE", url);
    }

    public DownloadResponse(URL url) {
        this("UNKNOWN", url);
    }

    private DownloadResponse(String string, URL url) {
        this.result = String.format("%-15s: %s", string, url.toString());
    }

}
