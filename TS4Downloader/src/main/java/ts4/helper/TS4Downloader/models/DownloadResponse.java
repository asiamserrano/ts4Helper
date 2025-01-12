package ts4.helper.TS4Downloader.models;

public class DownloadResponse {

    public final String result;

    public DownloadResponse(boolean bool) {
        this.result = bool ? "SUCCESSFUL" : "FAILURE";
    }

    public DownloadResponse(String result) {
        this.result = result;
    }

}
