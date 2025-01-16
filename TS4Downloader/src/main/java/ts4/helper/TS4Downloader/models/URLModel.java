package ts4.helper.TS4Downloader.models;

import java.net.URL;

public class URLModel {

    public final URL url;
    public final URLModel previous;

    public URLModel(URL url) {
        this.url = url;
        this.previous = null;
    }

    public URLModel(URL url, URLModel previous) {
        this.url = url;
        this.previous = previous;
    }

    @Override
    public String toString() {
        String urlString = url.toString();
        if (previous == null) {
            return urlString;
        } else {
            return String.format("%s -> %s", urlString, previous);
        }
    }

}
