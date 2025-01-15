package ts4.helper.TS4Downloader.utilities;

import okhttp3.*;
import ts4.helper.TS4Downloader.enums.DomainEnum;
import ts4.helper.TS4Downloader.enums.WebsiteEnum;

import java.net.URL;

import static ts4.helper.TS4Downloader.constants.OkHttpConstants.USER_AGENT_HEADER;
import static ts4.helper.TS4Downloader.constants.OkHttpConstants.USER_AGENT_VALUE;
import static ts4.helper.TS4Downloader.constants.StringConstants.FORWARD_SLASH;

import static ts4.helper.TS4Downloader.constants.OkHttpConstants.HTTPS_SCHEME;

import static ts4.helper.TS4Downloader.enums.DomainEnum.FORGE_CDN;

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

    public static HttpUrl createHttpUrl(DomainEnum domainEnum) {
        String domain = domainEnum == FORGE_CDN ? "edge" : "www";
        return createHttpUrl(HTTPS_SCHEME, String.format("%s.%s", domain, domainEnum.name));
    }

    public static HttpUrl createHttpUrl(String scheme, String host) {
        return new HttpUrl.Builder()
                .scheme(scheme)
                .host(host)
                .build();
    }

    public static Cookie createCookie(String cookie, DomainEnum domainEnum) {
        HttpUrl httpUrl = createHttpUrl(domainEnum);
        return new Cookie.Builder()
                .domain(httpUrl.host())
                .path(FORWARD_SLASH)
                .name("cookie-name")
                .value(cookie)
                .httpOnly()
                .secure()
                .build();
    }

}
