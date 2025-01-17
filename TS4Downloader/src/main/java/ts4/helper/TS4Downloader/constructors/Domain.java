package ts4.helper.TS4Downloader.constructors;

import lombok.AllArgsConstructor;

@AllArgsConstructor
public abstract class Domain {
    public final String value;

    @Override
    public String toString() {
        return this.value;
    }

}