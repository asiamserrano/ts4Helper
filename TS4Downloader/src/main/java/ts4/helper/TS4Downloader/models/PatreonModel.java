package ts4.helper.TS4Downloader.models;

import lombok.AllArgsConstructor;

import java.io.File;
import java.net.URL;

@AllArgsConstructor
public class PatreonModel implements Comparable<PatreonModel> {

    public final URL source;
    public final File destination;

    private String sourceString() { return this.source.toString(); }
    private String destinationString() { return this.destination.getAbsolutePath(); }
    @Override
    public boolean equals(Object object) {
        if (object instanceof PatreonModel that) {
            return this.sourceString().equals(that.sourceString()) &&
                    this.destinationString().equals(that.destinationString());
        } else {
            return false;
        }
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + this.sourceString().hashCode();
        result = prime * result + this.destinationString().hashCode();
        return result;
    }

    @Override
    public int compareTo(PatreonModel that) {
        if (this.sourceString().equals(that.sourceString())) {
            return this.destinationString().compareTo(that.destinationString());
        } else {
            return this.sourceString().compareTo(that.sourceString());
        }
    }

}
