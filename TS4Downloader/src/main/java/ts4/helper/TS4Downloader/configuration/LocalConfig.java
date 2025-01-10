package ts4.helper.TS4Downloader.configuration;

import okhttp3.CookieJar;
import okhttp3.Cookie;
import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.web.client.RestTemplate;
import ts4.helper.TS4Downloader.utilities.StringUtility;

import java.util.List;
import java.util.ArrayList;
import java.util.HashMap;

import static ts4.helper.TS4Downloader.constants.ConfigConstants.PROFILE;
import static ts4.helper.TS4Downloader.constants.ConfigConstants.REST_TEMPLATE_BEAN;
import static ts4.helper.TS4Downloader.constants.ConfigConstants.COOKIE_JAR_BEAN;
import static ts4.helper.TS4Downloader.constants.ConfigConstants.OK_HTTP_CLIENT_BEAN;


@Profile(PROFILE)
@Configuration
public class LocalConfig {

    private static final Logger log = LoggerFactory.getLogger(LocalConfig.class);

    @Value("${spring.application.curseforge.cookie}")
    private String curseForgeCookie;

    @Bean(name = REST_TEMPLATE_BEAN)
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }

    @Bean(name = COOKIE_JAR_BEAN)
    public CookieJar cookieJar() {
        return new CookieJar() {
            private final HashMap<HttpUrl, List<Cookie>> cookieStore = new HashMap<>();

            @Override
            public void saveFromResponse(@NotNull HttpUrl url, @NotNull List<Cookie> cookies) {
                cookieStore.put(url, cookies);
            }

            @NotNull
            @Override
            public List<Cookie> loadForRequest(@NotNull HttpUrl url) {
                List<Cookie> cookies = cookieStore.get(url);
                return cookies != null ? cookies : new ArrayList<Cookie>();
            }
        };
    }

//    @Bean(name = "client")
//    public OkHttpClient client(final CookieJar cookieJar) {
//        return new OkHttpClient.Builder().cookieJar(cookieJar).build();
//    }

    @Bean(name = OK_HTTP_CLIENT_BEAN)
    public OkHttpClient client() {
        try {
            String cookie = StringUtility.loadResource(curseForgeCookie);
            log.info("cookie retrieved: {}", cookie);
            return new OkHttpClient().newBuilder()
                    .addInterceptor(chain -> {
                        final Request original = chain.request();
                        final Request authorized = original.newBuilder()
                                .addHeader("Cookie", cookie)
                                .build();
                        return chain.proceed(authorized);
                    })
                    .build();
        } catch (Exception e) {
            log.error("unable to create OkHttpClient {}", e.getMessage());
            throw new RuntimeException();
        }
    }

}
