package ts4.helper.TS4Downloader.utilities;

import okhttp3.Request;
import okhttp3.Response;
import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;
import okhttp3.Call;

import java.net.URL;

public abstract class OkHttpUtility {

    public static HttpUrl extract(HttpUrl url) {
        return new HttpUrl.Builder()
                .scheme(url.scheme())
                .host(url.host())
                .build();
    }

    public static Response sendRequest(String url, OkHttpClient client) throws Exception {
        return sendRequest(URLUtility.createURL(url), client);
    }

    public static Response sendRequest(URL url, OkHttpClient client) throws Exception {
        Request request = new Request.Builder()
                .header("User-Agent", "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_15_7) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/131.0.0.0 Safari/537.36")
                .url(url)
                .build();
        Call call = client.newCall(request);
        return call.execute();
    }

}
