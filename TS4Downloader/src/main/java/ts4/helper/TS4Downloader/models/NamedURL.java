package ts4.helper.TS4Downloader.models;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

import java.net.URL;

import static ts4.helper.TS4Downloader.constants.StringConstants.EMPTY;

@AllArgsConstructor
public class NamedURL {

    public final String name;
    public final URL url;

    public NamedURL(URL url) {
        this.name = null;
        this.url = url;
    }

    public NamedURL(NamedURL namedURL, URL url) {
        this.name = namedURL.name;
        this.url = url;
    }

    @Override
    public String toString() {
        String nm = name == null ? "null" : name;
        return String.format("%-100s%s", nm, url);
    }

}
