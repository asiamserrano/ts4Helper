package ts4.helper.TS4Downloader;

import com.fasterxml.jackson.annotation.JsonProperty;
import kotlin.Pair;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import okhttp3.Headers;
import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;
import okhttp3.Response;
import okhttp3.internal.http2.Header;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;
import ts4.helper.TS4Downloader.models.RetryURL;
import ts4.helper.TS4Downloader.utilities.*;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.math.BigInteger;
import java.net.URI;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLStreamHandler;
import java.nio.channels.Channels;
import java.nio.channels.FileChannel;
import java.nio.channels.ReadableByteChannel;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Paths;
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
//        URL url = URLUtility.createURL("https://www.patreon.com/posts/56379158");
        URL source = URLUtility.createURL("https://www.patreon.com/posts/wire-high-heels-113597759");

        Response response = OkHttpUtility.sendRequest(source, new OkHttpClient());
        String content = response.body().string();
        
//        String content = response.body().string();
//        response.close();
//        System.out.println(content);


//        Matcher matcher = StringUtility.getRegexBetweenMatcher(content,"\u0000", ".");
//        while (matcher.find()) {
//            System.out.println(matcher.group());
//        }


//        try(FileOutputStream fileOutputStream = new FileOutputStream(destination)) {
//            ReadableByteChannel readableByteChannel = Channels.newChannel(source.openStream());
//            FileChannel fileChannel = fileOutputStream.getChannel();
//            fileChannel.transferFrom(readableByteChannel, 0, Long.MAX_VALUE);
//        } catch (Exception e) {
//            log.error("unable to download url {} to {}", source, destination, e);
//        }
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