package ts4.helper.TS4Downloader;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;
import okhttp3.Response;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;
import ts4.helper.TS4Downloader.models.RetryURL;
import ts4.helper.TS4Downloader.utilities.OkHttpUtility;
import ts4.helper.TS4Downloader.utilities.StringUtility;
import ts4.helper.TS4Downloader.utilities.URLUtility;

import java.io.IOException;
import java.math.BigInteger;
import java.net.URI;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLStreamHandler;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.regex.Matcher;
import java.util.stream.Collectors;

import static ts4.helper.TS4Downloader.constants.StringConstants.*;
import static ts4.helper.TS4Downloader.constants.OkHttpConstants.HTTPS_SCHEME;
//import static ts4.helper.TS4Downloader.enums.WebsiteEnum.CURSE_FORGE_CAS;
import static ts4.helper.TS4Downloader.enums.DomainEnum.CURSE_FORGE;

import static ts4.helper.TS4Downloader.enums.ResponseEnum.DOWNLOAD;

@Slf4j
public class Main {

    @SuppressWarnings("unchecked")
    public static void main(String[] args) throws Exception {
        String content = StringUtility.loadResource("files/output.json");
        JSONObject jsonObject = (JSONObject) JSONValue.parse(content);

        List<URL> list = ((List<String>) jsonObject.get(DOWNLOAD.toString())).stream().map(Main::createURL).toList();
        List<RetryURL> urls = list.stream().map(RetryURL::new).toList();
//        doWork(urls);

        URL url = urls.getLast().url;
        Response response = OkHttpUtility.sendRequest(url, new OkHttpClient());
        System.out.println(response.isSuccessful());

    }

    private static void doWork(List<RetryURL> urls) throws Exception {
        if (urls.isEmpty()) {
            log.info("DONE DOWNLOADING URLS");
        } else {
            List<RetryURL> list = new ArrayList<>();
            Response response;
            for (RetryURL retry : urls) {
                response = OkHttpUtility.sendRequest(retry.url, new OkHttpClient());
                if (response.isSuccessful()) {
                    log.info(response.toString());
                } else {
                    if (retry.isUnderLimit()) {
                        list.add(retry);
                    } else {
                        log.info("retries limit reached for url {}", retry.url);
                    }
                    retry.increment();
                }
                response.close();
            }
            doWork(list);
        }
    }

    private static URL createURL(String url) {
        try {
            return URLUtility.createURL(url);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

}

//@Getter
//@Setter
//@AllArgsConstructor
//class SimsFindsParameters {
//
//    @JsonProperty("flid")
//    private String flid;
//
//    @JsonProperty("cid")
//    private String cid;
//
//    @JsonProperty("key")
//    private String key;
//
//    @JsonProperty("version")
//    private String version;
//
//    @JsonProperty("pass")
//    private String pass;
//
//
//}