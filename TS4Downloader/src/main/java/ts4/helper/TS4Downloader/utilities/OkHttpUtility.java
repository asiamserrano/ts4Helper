package ts4.helper.TS4Downloader.utilities;

import okhttp3.*;
import ts4.helper.TS4Downloader.enums.WebsiteEnum;

import java.net.URL;

import static ts4.helper.TS4Downloader.constants.OkHttpConstants.USER_AGENT_HEADER;
import static ts4.helper.TS4Downloader.constants.OkHttpConstants.USER_AGENT_VALUE;
import static ts4.helper.TS4Downloader.constants.StringConstants.FORWARD_SLASH;

import static ts4.helper.TS4Downloader.constants.OkHttpConstants.HTTPS_SCHEME;

public abstract class OkHttpUtility {

    public static Response sendRequest(HttpUrl httpUrl, OkHttpClient client) throws Exception {
        URL url = URLUtility.createURL(httpUrl.toString());
        return sendRequest(url, client);
    }

    public static Response sendRequest(URL url, OkHttpClient client) throws Exception {
        Request request = new Request.Builder()
                .header(USER_AGENT_HEADER, USER_AGENT_VALUE)
                .url(url)
                .build();
        Call call = client.newCall(request);
        return call.execute();
    }

    public static String getContent(URL url, OkHttpClient client) throws Exception {
        Response response = sendRequest(url, client);
        String string = response.body().string();
        response.close();
        return string;
    }

//    public static HttpUrl extract(HttpUrl url) {
//        return createHttpUrl(url.scheme(), url.host());
//    }

    public static HttpUrl createHttpUrl(HttpUrl httpUrl) {
        return createHttpUrl(httpUrl.scheme(), httpUrl.host());
    }

    public static HttpUrl createHttpUrl(WebsiteEnum websiteEnum) {
        return createHttpUrl(HTTPS_SCHEME, websiteEnum.domain);
    }

    public static HttpUrl createHttpUrl(String scheme, String host) {
        return new HttpUrl.Builder()
                .scheme(scheme)
                .host(host)
                .build();
    }

    public static Cookie createCookie(String cookie, WebsiteEnum websiteEnum) {
        return new Cookie.Builder()
                .domain(websiteEnum.domain)
                .path(FORWARD_SLASH)
                .name("cookie-name")
                .value(cookie)
                .httpOnly()
                .secure()
                .build();
    }

}
