package ts4.helper.TS4Downloader.models;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.json.simple.JSONObject;
import ts4.helper.TS4Downloader.utilities.URLUtility;

import java.net.URL;

import static ts4.helper.TS4Downloader.constants.StringConstants.BACK_SLASHES;
import static ts4.helper.TS4Downloader.constants.StringConstants.EMPTY;
import static ts4.helper.TS4Downloader.constants.StringConstants.UNCHECKED;

@AllArgsConstructor
@Slf4j
public class WebsiteModel {

    private enum Keys {
        url, name, previous
    }

    public final URL url;
    public final String name;
    public final WebsiteModel previous;

    public WebsiteModel(String url) {
        this.url = URLUtility.createURL(url);
        this.name = EMPTY;
        this.previous = null;
    }

    public WebsiteModel(String url, String name, WebsiteModel previous) {
        this.url = URLUtility.createURL(url);
        this.name = name;
        this.previous = previous;
    }

    public WebsiteModel(JSONObject jsonObject) {
        this.url = URLUtility.createURL(jsonObject.get(Keys.url.toString()).toString());
        this.name = jsonObject.get(Keys.name.toString()).toString();
        Object object = jsonObject.get(Keys.previous.toString());
        if (object instanceof JSONObject) {
            this.previous = new WebsiteModel((JSONObject) object);
        } else {
            this.previous = null;
        }
    }

    @Override
    public String toString() {
        return this.toJSONObject().toString().replaceAll(BACK_SLASHES, EMPTY);
    }

    @SuppressWarnings(UNCHECKED)
    private JSONObject toJSONObject() {
        JSONObject json = new JSONObject();
        json.put(Keys.url.toString(), url.toString());
        json.put(Keys.name.toString(), name);
        json.put(Keys.previous.toString(), previous == null ? null : previous.toJSONObject());
        return json;
    }

}
