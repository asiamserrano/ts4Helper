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
import org.springframework.web.util.UriComponentsBuilder;
import ts4.helper.TS4Downloader.models.NamedURL;
import ts4.helper.TS4Downloader.models.NestedURL;
import ts4.helper.TS4Downloader.models.RetryURL;
import ts4.helper.TS4Downloader.utilities.*;

import javax.naming.Name;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.math.BigInteger;
import java.net.*;
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

    public static final OkHttpClient client = new OkHttpClient();
    @SuppressWarnings("unchecked")
    public static void main(String[] args) throws Exception {
//        String content = StringUtility.loadResource("html_file.html");
        // <title id="title">helgatisha Recolor EP03 Romper - The Sims 4 Download - SimsFinds</title>
//        String name = StringUtility.getStringBetweenRegex(content, "<title id=\"title\">", "</title>");
//        System.out.println(name);
        //String content = StringUtility.loadResource("html_file.html");

        //https://www.simsfinds.com/downloads/316599/la-medusa-plataform-sandals-sims4
        // https://www.simsfinds.com/downloads/151949/helgatisha-recolor-ep03-romper-sims4

//        String start = "https://www.simsfinds.com/downloads/151949/helgatisha-recolor-ep03-romper-sims4";
//        String start = "https://www.simsfinds.com/downloads/316599/la-medusa-plataform-sandals-sims4";


        //https://www.curseforge.com/sims4/create-a-sim/eyelashes-part-1-2-and-3

//        String string = "https://www.curseforge.com/sims4/create-a-sim/eyelashes-part-1-2-and-3";

        //https://www.curseforge.com/api/v1/mods/669528/files/5976951/download

//        List<NamedURL> namedURLS = getCurseForgeNamedURLs(string);

//        List<NamedURL> namedURLS = getPatreonNamedURLs("https://www.patreon.com/posts/56379158");
//        for(NamedURL namedURL: namedURLS) System.out.println(namedURL);

        //https://alenaivanisova.my.curseforge.com/?projectsPage=1&projectsSearch=&projectsSort=9

        String content = StringUtility.loadResource("html_file.html");

        List<NamedURL> list = getCurgeForgeEdgeNamedURLs2(content);

        for(NamedURL namedURL : list) System.out.println(namedURL);

    }

    private static List<NamedURL> getCurgeForgeEdgeNamedURLs(String url) {
        URL source = URLUtility.createURL(url);
        String content = OkHttpUtility.getContent(source, new OkHttpClient());
        return StringUtility.getSetBetweenRegex(content, "\"downloadLink\":\"", SINGLE_QUOTE)
                .stream()
                .map(Main::getCurgeForgeEdgeNamedURL)
                .toList();
    }

    private static List<NamedURL> getCurgeForgeEdgeNamedURLs2(String content) {
        return StringUtility.getSetBetweenRegex(content, "\"downloadLink\":\"", SINGLE_QUOTE)
                .stream()
                .map(Main::getCurgeForgeEdgeNamedURL)
                .toList();
    }

    private static NamedURL getCurgeForgeEdgeNamedURL(String str) {
        try {
            String string = str.replaceAll(BACK_SLASHES, EMPTY);
            String[] parts = string.split("/");
            String filename = URLDecoder.decode(parts[parts.length - 1], StandardCharsets.UTF_8);
            URL url = URLUtility.createURL(string);
            return new NamedURL(filename, url);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private static List<NamedURL> getCurseForgAPIeNamedURLs(String start) {
        URL source = URLUtility.createURL(start);
        String content = OkHttpUtility.getContent(source, new OkHttpClient());

        String projectId = StringUtility.getStringBetweenRegex(content, "Project ID</dt><dd>", "</dd>");
//        System.out.println(projectId);

        String info = StringUtility.getStringBetweenRegex(content, "\"mainFile\":{\"id\":", ",\"displayName\"");
//        System.out.println(info);

        String id = info.split(",")[0];
        String filename = info.split("fileName:")[1];

        URL url = URLUtility.createURL(String.format("https://www.curseforge.com/api/v1/mods/%s/files/%s/download", projectId, id));

        NamedURL namedURL = new NamedURL(filename, url);

        return Collections.singletonList(namedURL);
    }

    private static List<NamedURL> getSimsFindsNamedURLs(String url) {
        OkHttpClient client = new OkHttpClient();

        Response response;
        String content;
        NamedURL namedURL;

        URL downloadsURL = URLUtility.createURL(url);
        System.out.println(downloadsURL);

        content = OkHttpUtility.getContent(downloadsURL, client);

        String name = StringUtility.getStringBetweenRegex(content, "<title id=\"title\">", "</title>");
        String continue_string = StringUtility.getStringBetweenRegex(content, "data-continue=\"", "\"");
        URL continueURl = URLUtility.createURL(continue_string);
        System.out.println(continueURl);

        content = OkHttpUtility.getContent(continueURl, client);

        String SQ = SINGLE_QUOTE;
        String[] info = StringUtility.getStringBetweenRegex(content, "data-at5t768r9=\"", SQ).split(COMMA);
        String flid = StringUtility.getStringBetweenRegex(content, "data-at8r136r7=\"", SQ);
        String pass = StringUtility.getStringBetweenRegex(content, "data-passe=\"", SQ);
        Map<String, String> map = new HashMap<>() {{
            put("cid", info[0]);
            put("key", info[1]);
            put("version", info[3]);
            put("pass", pass);
            put("flid", flid);
        }};

        List<String> list = map.entrySet().stream()
                .map(e -> String.format("%s=%s", e.getKey(), e.getValue()))
                .collect(Collectors.toList());

        URL downloadURL = URLUtility.createURL("https://click.simsfinds.com/download?" + String.join(AMPERSAND, list));

        if (downloadURL.toString().contains("flid=0")) {
            System.out.println(downloadURL);

            content = OkHttpUtility.getContent(downloadURL, client);

            URL externalURL = URLUtility.createURL(StringUtility.getStringBetweenRegex(content, "<title>", "</title"));
            namedURL = new NamedURL(externalURL);

        } else {
            namedURL = new NamedURL(name, downloadURL);
        }

        return Collections.singletonList(namedURL);
    }

    private static List<NamedURL> getPatreonNamedURLs(String url) {
        URL source = URLUtility.createURL(url);
        String content = OkHttpUtility.getContent(source, new OkHttpClient());
        String folder = StringUtility.getStringBetweenRegex(content, "<title>", "</title>");
        NamedURL namedURL = new NamedURL(folder, source);
        List<NamedURL> list = StringUtility.getSetBetweenRegex(content, "{\"attributes\":{\"name\":\"", "\"},")
                .stream().map(Main::getPatreonNamedURL).toList();
        System.out.println(namedURL);
        return list;
    }

    private static NamedURL getPatreonNamedURL(String s) {
        String filename = s.split(",")[0];
        String url_string = s.split("url:")[1].replace("\\u0026i", "&i");
        try {
            URL url = URLUtility.createURL(url_string);
            return new NamedURL(filename, url);
        } catch (Exception e) {
            log.error("could not create named url: {} {}", filename, url_string, e);
            throw new RuntimeException();
        }
    }

    private static void doWork(List<RetryURL> urls) {
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