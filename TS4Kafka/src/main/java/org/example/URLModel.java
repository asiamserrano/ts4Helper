package org.example;

import lombok.AllArgsConstructor;
import org.json.simple.JSONObject;

import java.io.Serializable;
import java.net.URL;

@AllArgsConstructor
public class URLModel implements Serializable {

    public final URL url;
    public final String name;
    public final URLModel previous;

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

}
