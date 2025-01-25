package org.example.ts4package.models;

import lombok.extern.slf4j.Slf4j;
import org.example.ts4package.utilities.URLUtility;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;

import java.net.URL;
import java.util.Arrays;

import static org.example.ts4package.constants.StringConstants.BACK_SLASHES;
import static org.example.ts4package.constants.StringConstants.EMPTY;
import static org.example.ts4package.constants.StringConstants.UNCHECKED;

@Slf4j
public class WebsiteModel {

    public static class JSON extends JSONObject {

        @SuppressWarnings(UNCHECKED)
        public JSON put(Keys keys, Object value) {
            this.put(keys.name(), value);
            return this;
        }

        public Object get(Keys keys) {
            Object object = this.get(keys.name());
            String value = object == null ? null : object.toString();
            return value == null ? null : keys == Keys.previous ? Builder.parse(value) : value;
        }

        @Override
        public String toString() {
            return this.toJSONString().replaceAll(BACK_SLASHES, EMPTY);
        }

    }

    public static class Builder {

        private final String url;
        private String name;
        private Builder previous;

        private Builder(String url, String name, Builder previous) {
            this.url = url;
            this.name = name;
            this.previous = previous;
        }

        private Builder(JSON json) {
            this.url = json.get(Keys.url).toString();
            this.name = json.get(Keys.name).toString();
            this.previous = (Builder) json.get(Keys.previous);
        }

        public Builder(String url) {
            this(url, null, null);
        }

        public Builder(URL url) {
            this(url.toString(), null, null);
        }

        public static Builder parse(String message) {
            try {
                JSON json = new JSON();
                JSONObject jsonObject = (JSONObject) JSONValue.parse(message);
                Arrays.stream(Keys.values()).forEach(key -> json.put(key, jsonObject.get(key.toString())));
                return new Builder(json);
            } catch (Exception e) {
                log.error("unable to parse JSON object {}: {}", message, e.getMessage());
                throw new RuntimeException(e);
            }
        }

        public Builder setName(String name) {
            this.name = name.strip();
            return this;
        }

        public Builder setPrevious(WebsiteModel model) {
            this.previous = model == null ? null : model.builder();
            return this;
        }

        public WebsiteModel build() {
            try {
                URL u = URLUtility.createURLException(this.url);
                String n = this.name == null ? EMPTY : this.name;
                WebsiteModel p = this.previous == null ? null : this.previous.build();
                return new WebsiteModel(u, n, p);
            } catch (Exception e) {
                log.error("error when building model: {}", e.getMessage());
                return null;
            }
        }

        @Override
        public String toString() {
            return this.toJSONModel().toString();
        }

        protected JSON toJSONModel() {
            return new JSON()
                    .put(Keys.url, this.url)
                    .put(Keys.name, this.name)
                    .put(Keys.previous, previous == null ? null : previous.toJSONModel());
        }

    }

    public enum Keys {
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
        return this.builder().toString();
    }

    protected Builder builder() {
        return new Builder(url).setName(name).setPrevious(previous);
    }

}
