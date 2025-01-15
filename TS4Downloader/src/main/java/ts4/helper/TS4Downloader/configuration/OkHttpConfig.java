package ts4.helper.TS4Downloader.configuration;

import lombok.extern.slf4j.Slf4j;
import okhttp3.CookieJar;
import okhttp3.HttpUrl;
import okhttp3.Cookie;
import okhttp3.OkHttpClient;
import org.jetbrains.annotations.NotNull;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import ts4.helper.TS4Downloader.utilities.OkHttpUtility;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static ts4.helper.TS4Downloader.constants.ConfigConstants.PROFILE;
import static ts4.helper.TS4Downloader.constants.ConfigConstants.COOKIE_JAR_BEAN;
import static ts4.helper.TS4Downloader.constants.ConfigConstants.OK_HTTP_CLIENT_BEAN;

@Profile(PROFILE)
@Configuration
@Slf4j
public class OkHttpConfig {

    @Bean(name = COOKIE_JAR_BEAN)
    public CookieJar cookieJar() {
        return new CookieJar() {

            private final HashMap<HttpUrl, List<Cookie>> cookieStore = new HashMap<>();

            @Override
            public void saveFromResponse(@NotNull HttpUrl url, @NotNull List<Cookie> cookies) {
                HttpUrl extracted = OkHttpUtility.createHttpUrl(url);
                cookieStore.put(extracted, cookies);
            }

            @NotNull
            @Override
            public List<Cookie> loadForRequest(@NotNull HttpUrl url) {
                HttpUrl extracted = OkHttpUtility.createHttpUrl(url);
                List<Cookie> cookies = cookieStore.get(extracted);
                return cookies != null ? cookies : new ArrayList<Cookie>();
            }

        };
    }

    @Bean(name = OK_HTTP_CLIENT_BEAN)
    public OkHttpClient client(final CookieJar cookieJar) {
        try {
            return new OkHttpClient().newBuilder().cookieJar(cookieJar).build();
        } catch (Exception e) {
            log.error("unable to create OkHttpClient {}", e.getMessage());
            throw new RuntimeException();
        }
    }

}
