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
    public OkHttpClient client(final CookieJar cookieJar) {
        try {
            URL cookieUrl = Resources.getResource(curseForgeCookie);
            String cookie = Resources.toString(cookieUrl, StandardCharsets.UTF_8);
            log.info("cookie retrieved: {}", cookie);
            return new OkHttpClient().newBuilder()
                    .addInterceptor(chain -> {
                        final Request original = chain.request();
                        final Request authorized = original.newBuilder()
                                .addHeader("Cookie", cookie)
//                            .addHeader("Cookie", "_gcl_au=1.1.504560386.1736217365; _lr_env_src_ats=false; _scor_uid=d72c994cf28844d08a39d805b1277c69; nitro-uid_cst=VyxHLMwsHQ%3D%3D; ncmp.domain=curseforge.com; _cc_id=5d0f0cf304efad30fd5ebd5cd73220d3; panoramaId_expiry=1736822165512; panoramaId=9ef9235774bffc6745401e4dfc1e16d539381ded3772c1c1befa28de3aa09892; panoramaIdType=panoIndiv; _au_1d=AU1D-0100-001736217366-MEMXP4CZ-XNE3; cf_cookieBarHandled=true; __mmapiwsid=01943ee4-a13c-78f1-9e8a-0a67563cbeb2:ce1fdd5d65e0861d7d665a578abec7db85208bb6; mp_7f5cd212660fa0bc40eb2cd1335ede5c_mixpanel=%7B%22distinct_id%22%3A%20%22%24device%3A1943ee49dee4d3-05fc900ef6590d-1e525636-16a7f0-1943ee49dee4d3%22%2C%22%24device_id%22%3A%20%221943ee49dee4d3-05fc900ef6590d-1e525636-16a7f0-1943ee49dee4d3%22%2C%22%24initial_referrer%22%3A%20%22https%3A%2F%2Fwww.curseforge.com%2F%22%2C%22%24initial_referring_domain%22%3A%20%22www.curseforge.com%22%7D; Unique_ID_v2=40f52bbee77a4f5b8d46d44aa7e2d3bc; __utma=94490894.905560026.1736217365.1736223879.1736316787.2; __utmz=94490894.1736316787.2.2.utmcsr=patreon.com|utmccn=(referral)|utmcmd=referral|utmcct=/; _ga=GA1.1.905560026.1736217365; _ga_N8BTN266HQ=GS1.1.1736316786.2.1.1736317509.0.0.0; _clck=1km9g0i%7C2%7Cfsg%7C0%7C1833; nitro-uid=%7B%22TDID%22%3A%22598f3b6c-b47d-4be8-b9db-9707f6c1e90c%22%2C%22TDID_LOOKUP%22%3A%22TRUE%22%2C%22TDID_CREATED_AT%22%3A%222024-12-10T04%3A05%3A56%22%7D; _iiq_fdata=%7B%22pcid%22%3A%22c6888d22-9747-6255-97a4-83ed2ea4ca15%22%2C%22pcidDate%22%3A1736217365280%7D; _lr_geo_location_state=NJ; _lr_geo_location=US; _ga_FVWZ0RM4DH=GS1.1.1736485622.11.0.1736485622.60.0.0; __cf_bm=IW9nkEdVh8bbsugOalFyPMGDL23YUzmYSl.yJsMtL8c-1736489777-1.0.1.1-xzXLXDkgHBoxAXX__F4xQ7bJQHAeTVY_9LVlmfpucXDWNCqcj7AVTvh734K.K_zH8SYvyh7ZBuXgJZwSQXGii2Fwb3xnbfvaYBTQ41Tiz3I; _lr_retry_request=true; _ga_LM2NHZZNX1=GS1.1.1736490002.11.0.1736490002.0.0.0; _rdt_uuid=1736217364925.4ca25c97-bd9f-4198-a97f-b043e3dc1ac1; cto_bidid=9Cmb_l9QR0xETkNBRE8lMkYxbGNFekZUa1ZRMGUlMkJwRlk1RCUyQkVqbW9VRU10d2EyWjZVWG8lMkZNck5mVkw1Q1NRMlBRZHVZcjElMkJTVlVESEFHWFJpeTBxR1RPbTk4aGdqdzBzb2RmUndicG1CVncwVlB6aSUyRmdGNUNJWkkwWGlxb3pUY3RUMzdlUWclMkZaZW5ickx6YVR1cEtScUdVVk5pQSUzRCUzRA; _uetsid=316a78c0cf0811ef8c7ae3ca9446a607; _uetvid=2470e7f0cca011ef9dd0272f2328fa5c; _clsk=1wh67mi%7C1736490002938%7C1%7C0%7Ct.clarity.ms%2Fcollect; __gads=ID=11b8fc3b303c625d:T=1736217367:RT=1736490004:S=ALNI_MYqRzKjePahinqPA44U4aYpFn_F_Q; __gpi=UID=00000fc72d218b88:T=1736217367:RT=1736490004:S=ALNI_MYVTU2JmBcz_t33hBDBYDujBIzwgA; __eoi=ID=5f954e3fdd83b13f:T=1736217367:RT=1736490004:S=AA-Afja9aD-FJ7QGRPpZH7VdAmvy; _ga_07LHW959W7=GS1.1.1736490002.11.0.1736490004.0.0.0; cto_bundle=nHeEq18zJTJCcFNHSWVudmppVE05b0h2MmNrQ09UWTJ5OXZMb1l3ZnJkbnNUNVVxN0tsU25OQ0xaUUQlMkJGNlBaQ3d3akV1NlRneUVmVFQ3JTJGVFFQNHprRTU3c1AlMkJVdGNQN1ZVZ3FHZXZDTjRWOXBxSGRGb0ZJam5BdjhlM2wxYXVsRUYwMEdMSm9TZHolMkI5UiUyRnZhNnBydlpHdTRYQWNMU2hDa29FWU85ZDR1UHJqUzdQWENyNFRMcU9EbkViQTc2cGxjNFhhT1lTeDhTZHRaalVMbzlhTmtlNERaMSUyRnclM0QlM0Q")
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
