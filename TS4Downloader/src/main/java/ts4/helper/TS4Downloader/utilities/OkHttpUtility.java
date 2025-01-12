package ts4.helper.TS4Downloader.utilities;

import okhttp3.Request;
import okhttp3.Response;
import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;
import okhttp3.Call;

import java.net.URL;

import static ts4.helper.TS4Downloader.constants.OkHttpConstants.USER_AGENT_HEADER;
import static ts4.helper.TS4Downloader.constants.OkHttpConstants.USER_AGENT_VALUE;

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

    public static HttpUrl extract(HttpUrl url) {
        return create(url.scheme(), url.host());
    }

    public static HttpUrl create(String scheme, String host) {
        return new HttpUrl.Builder()
                .scheme(scheme)
                .host(host)
                .build();
    }

}
