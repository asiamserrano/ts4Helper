package ts4.helper.TS4Downloader.models;

import lombok.AllArgsConstructor;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;
import ts4.helper.TS4Downloader.utilities.URLUtility;

import java.io.Serializable;
import java.net.URL;

import static ts4.helper.TS4Downloader.constants.OkHttpConstants.HTTPS_SCHEME;
import static ts4.helper.TS4Downloader.constants.StringConstants.BACK_SLASHES;
import static ts4.helper.TS4Downloader.constants.StringConstants.EMPTY;

@AllArgsConstructor
public class URLModel implements Serializable {

    public final URL url;
    public final String name;
    public final URLModel previous;

    @SuppressWarnings("unchecked")
    public URLModel(JSONObject jsonObject) {
        this.url = URLUtility.createURL((String) jsonObject.get("url"));
        this.name = (String) jsonObject.getOrDefault("name", EMPTY);
        JSONObject previous = (JSONObject) jsonObject.get("previous");
        if (previous == null) {
            this.previous = null;
        } else {
            this.previous = new URLModel(previous);
        }
    }

    public URLModel(URL url, String name) {
        this.url = url;
        this.name = name;
        this.previous = null;
    }

    @SuppressWarnings("unchecked")
    public JSONObject toJSON() {
        JSONObject json = new JSONObject();
        json.put("url", url.toString());
        if (!name.isEmpty()) json.put("name", name);
        if (previous != null) json.put("previous", previous.toJSON());
        return json;
    }

    @Override
    public String toString() {
        return toJSON().toString().replaceAll(BACK_SLASHES, EMPTY);
    }

}
