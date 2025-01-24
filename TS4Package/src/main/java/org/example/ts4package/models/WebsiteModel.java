package org.example.ts4package.models;

import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.json.simple.JSONObject;

import java.net.URI;
import java.net.URL;

import static org.example.ts4package.constants.StringConstants.BACK_SLASHES;
import static org.example.ts4package.constants.StringConstants.EMPTY;
import static org.example.ts4package.constants.StringConstants.UNCHECKED;

@Slf4j
public class WebsiteModel {

    @Getter @Setter
    public static class Builder {

        private String url;
        private String name;
        private Builder previous;

        public Builder() {
            this.url = null;
            this.name = null;
            this.previous = null;
        }

        public void setUrl(URL url) {
            this.url = url.toString();
        }

        public WebsiteModel build() {
            try {
                URL u = new URI(this.url).toURL();
                String n = this.name == null ? EMPTY : this.name;
                WebsiteModel p = this.previous.build();
                return new WebsiteModel(u, n, p);
            } catch (Exception e) {
                return null;
            }
        }

        public Builder(JSONObject jsonObject) {
            this.url = jsonObject.get(WebsiteModel.Keys.url.toString()).toString();
            this.name = jsonObject.get(WebsiteModel.Keys.name.toString()).toString();
            this.previous = fromJSON(jsonObject.get(WebsiteModel.Keys.previous.toString()));
        }

        public static Builder fromJSON(Object object) {
            if (object instanceof JSONObject) {
                return new Builder((JSONObject) object);
            } else {
                return null;
            }
        }

    }

    private enum Keys {
        url, name, previous
    }

    public final URL url;
    public final String name;
    public final WebsiteModel previous;

    private WebsiteModel (URL url, String name, WebsiteModel previous) {
        this.url = url;
        this.name = name;
        this.previous = previous;
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
