package ts4.helper.TS4Downloader.models;

import java.net.URL;

public class RetryURL {

    public final static int RETRIES_LIMIT = 3;

    private int retries;
    public URL url;

    public RetryURL(URL url) {
        this.url = url;
        this.retries = 0;
    }

    public void increment() {
        this.retries++;
    }

    public boolean isUnderLimit() {
        return this.retries < RETRIES_LIMIT;
    }

    public int getRetries() {
        return this.retries + 1;
    }

}
