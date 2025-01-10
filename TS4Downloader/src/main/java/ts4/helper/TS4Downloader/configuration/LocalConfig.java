package ts4.helper.TS4Downloader.configuration;

import com.google.common.io.Resources;
import okhttp3.*;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cglib.core.Local;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.web.client.RestTemplate;

import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.ArrayList;
import java.util.HashMap;

@Profile("local")
@Configuration
public class LocalConfig {

    private static final Logger log = LoggerFactory.getLogger(LocalConfig.class);

    @Bean(name = "restTemplate")
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }

    @Bean(name = "cookieJar")
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

    @Value("${spring.application.curseforge.cookie}")
    private String curseForgeCookie;

    @Bean(name = "client")
    public OkHttpClient client() {
        try {
            URL cookieUrl = Resources.getResource(curseForgeCookie);
            String cookie = Resources.toString(cookieUrl, StandardCharsets.UTF_8);
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
