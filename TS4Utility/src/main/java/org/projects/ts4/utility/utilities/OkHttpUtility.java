package org.projects.ts4.utility.utilities;

import lombok.extern.slf4j.Slf4j;
import okhttp3.*;
import org.projects.ts4.avro.WebsiteModel;
//import org.projects.ts4.utility.enums.WebsiteEnum;

import java.net.URL;

import static org.projects.ts4.utility.constants.OkHttpConstants.*;
import static org.projects.ts4.utility.constants.StringConstants.FORWARD_SLASH;

@Slf4j
public abstract class OkHttpUtility {

//    public static Response sendRequest(HttpUrl httpUrl, OkHttpClient client) {
//        try {
//            URL url = URLUtility.createURLNoException(httpUrl.toString());
//            return sendRequest(url, client);
//        } catch (Exception e) {
//            log.error("unable to send request for {}", httpUrl, e);
//            throw new RuntimeException(e);
//        }
//    }

    public static Response sendRequest(URL url, OkHttpClient client) {
        try {
            Request request = new Request.Builder()
                    .header(USER_AGENT_HEADER, USER_AGENT_VALUE)
                    .url(url)
                    .build();
            Call call = client.newCall(request);
            return call.execute();
        } catch (Exception e) {
            log.error("unable to send request for {}", url, e);
            throw new RuntimeException(e);
        }
    }

    public static String getContent(URL url, OkHttpClient client) {
        try {
            Response response = sendRequest(url, client);
            String string = response.body().string();
            response.close();
            return string;
        } catch (Exception e) {
            log.error("unable to get response for {}", url, e);
            throw new RuntimeException(e);
        }
    }

    public static Response sendRequest(WebsiteModel websiteModel, OkHttpClient client) {
        URL url = URLUtility.createURL(websiteModel);
        return sendRequest(url, client);
    }

    public static String getContent(WebsiteModel websiteModel, OkHttpClient client) {
        URL url = URLUtility.createURL(websiteModel);
        return getContent(url, client);
    }

//    public static HttpUrl extract(HttpUrl url) {
//        return createHttpUrl(url.scheme(), url.host());
//    }
//
//    public static HttpUrl createHttpUrl(HttpUrl httpUrl) {
//        return createHttpUrl(httpUrl.scheme(), httpUrl.host());
//    }

//    public static HttpUrl createHttpUrl(WebsiteEnum websiteEnum) {
//        return createHttpUrl(HTTPS_SCHEME, websiteEnum.getHost());
//    }

//    public static HttpUrl createHttpUrl(String scheme, String host) {
//        return new HttpUrl.Builder()
//                .scheme(scheme)
//                .host(host)
//                .build();
//    }
//
//    public static Cookie createCookie(String cookie, HttpUrl httpUrl) {
//        return new Cookie.Builder()
//                .domain(httpUrl.host())
//                .path(FORWARD_SLASH)
//                .name("cookie-name")
//                .value(cookie)
//                .httpOnly()
//                .secure()
//                .build();
//    }

}
