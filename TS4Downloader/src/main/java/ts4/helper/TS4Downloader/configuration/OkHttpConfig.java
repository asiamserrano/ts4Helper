package ts4.helper.TS4Downloader.configuration;

import lombok.extern.slf4j.Slf4j;
import okhttp3.CookieJar;
import okhttp3.HttpUrl;
import okhttp3.Cookie;
import okhttp3.OkHttpClient;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import ts4.helper.TS4Downloader.utilities.OkHttpUtility;
import ts4.helper.TS4Downloader.utilities.StringUtility;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

import static ts4.helper.TS4Downloader.constants.ConfigConstants.PROFILE;
import static ts4.helper.TS4Downloader.constants.ConfigConstants.COOKIE_JAR_BEAN;
import static ts4.helper.TS4Downloader.constants.ConfigConstants.CURSE_FORGE_COOKIE_BEAN;
import static ts4.helper.TS4Downloader.constants.ConfigConstants.OK_HTTP_CLIENT_BEAN;

import static ts4.helper.TS4Downloader.enums.WebsiteEnum.CURSE_FORGE;

import static ts4.helper.TS4Downloader.constants.StringConstants.FORWARD_SLASH;

@Profile(PROFILE)
@Configuration
@Slf4j
public class OkHttpConfig {

    @Value("${spring.application.curseforge.cookie}")
    private String curseForgeCookieFile;

    @Bean(name = COOKIE_JAR_BEAN)
    public CookieJar cookieJar(final Cookie curseForgeCookie) {
        return new CookieJar() {

            private final HashMap<HttpUrl, List<Cookie>> cookieStore = new HashMap<>() {{
                put(CURSE_FORGE.httpUrl, Collections.singletonList(curseForgeCookie));
            }};

            @Override
            public void saveFromResponse(@NotNull HttpUrl url, @NotNull List<Cookie> cookies) {
                HttpUrl extracted = OkHttpUtility.extract(url);
                cookieStore.put(extracted, cookies);
            }

            @NotNull
            @Override
            public List<Cookie> loadForRequest(@NotNull HttpUrl url) {
                HttpUrl extracted = OkHttpUtility.extract(url);
                List<Cookie> cookies = cookieStore.get(extracted);
                return cookies != null ? cookies : new ArrayList<Cookie>();
            }
        };
    }

    @Bean(name = CURSE_FORGE_COOKIE_BEAN)
    public Cookie curseForgeCookie() {
        try {
            String cookie = StringUtility.loadResource(curseForgeCookieFile);
            return new Cookie.Builder()
                    .domain(CURSE_FORGE.url)
                    .path(FORWARD_SLASH)
                    .name("cookie-name")
                    .value(cookie)
                    .httpOnly()
                    .secure()
                    .build();
        } catch(Exception e) {
            log.error("unable to create curseForgeCookie {}", e.getMessage());
            throw new RuntimeException();
        }
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
