package ts4.helper.TS4Downloader.models;

import ts4.helper.TS4Downloader.utilities.URLUtility;

import java.io.File;
import java.net.URL;

public class PatreonModel implements Comparable<PatreonModel> {

    public final URL source;
    public final File destination;

    private final String url;
    private final String file;

    public PatreonModel(URL source, File destination) {
        this.source = source;
        this.url = URLUtility.getURLString(source);
        this.destination = destination;
        this.file = destination.getAbsolutePath();
    }

    @Override
    public boolean equals(Object object) {
        if (object instanceof PatreonModel that) {
            return this.url.equals(that.url) && this.file.equals(that.file);
        } else {
            return false;
        }
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + this.url.hashCode();
        result = prime * result + this.file.hashCode();
        return result;
    }

    @Override
    public int compareTo(PatreonModel that) {
        if (this.url.equals(that.url)) {
            return this.file.compareTo(that.file);
        } else {
            return this.url.compareTo(that.url);
        }
    }

}
