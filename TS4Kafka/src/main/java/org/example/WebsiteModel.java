package org.example;

import lombok.AllArgsConstructor;
import org.json.simple.JSONObject;

import java.net.MalformedURLException;
import java.net.URL;

@AllArgsConstructor
public class WebsiteModel {

    public final URL url;
    public final String name;
    public final WebsiteModel previous;

    public WebsiteModel(String url) throws MalformedURLException {
        this.url = new URL(url);
        this.name = "";
        this.previous = null;
    }

    @Override @SuppressWarnings("unchecked")
    public String toString() {
        JSONObject json = new JSONObject();
        json.put("url", url.toString());
        json.put("name", name);
        json.put("previous", null);
        return json.toJSONString().replaceAll("\\\\", "");
    }

}
