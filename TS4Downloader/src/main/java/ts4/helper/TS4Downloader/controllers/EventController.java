package ts4.helper.TS4Downloader.controllers;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import okhttp3.OkHttpClient;
import okhttp3.Response;
import org.checkerframework.checker.units.qual.A;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestBody;

import java.io.File;
import java.net.URI;
import java.net.URL;
import java.net.http.HttpClient;
import java.net.http.HttpHeaders;
import java.net.http.HttpRequest;
import java.time.Duration;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.stream.Collectors;

import ts4.helper.TS4Downloader.enums.ResponseEnum;
import ts4.helper.TS4Downloader.enums.WebsiteEnum;
import ts4.helper.TS4Downloader.utilities.*;

import static ts4.helper.TS4Downloader.constants.ControllerConstants.EVENT_CONTROLLER_REQUEST_MAPPING;
import static ts4.helper.TS4Downloader.constants.ControllerConstants.EVENT_CONTROLLER_SAMPLE_GET_MAPPING;
import static ts4.helper.TS4Downloader.constants.ControllerConstants.EVENT_CONTROLLER_DOWNLOAD_LINKS_POST_MAPPING;
import static ts4.helper.TS4Downloader.constants.ControllerConstants.EVENT_CONTROLLER_CONSOLIDATE_POST_MAPPING;
import static ts4.helper.TS4Downloader.constants.StringConstants.*;
import static ts4.helper.TS4Downloader.enums.ResponseEnum.*;
//import static ts4.helper.TS4Downloader.enums.WebsiteEnum.CURSE_FORGE_CREATORS;
//import static ts4.helper.TS4Downloader.enums.WebsiteEnum.CURSE_FORGE_MEMBERS;


@RestController
@RequestMapping(EVENT_CONTROLLER_REQUEST_MAPPING)
@Slf4j
@AllArgsConstructor
public class EventController {

    private File nonDownloadedLinksFile;
    private OkHttpClient client;

    private static int RETRIES = 0;
    private static List<String> RESPONSES = new ArrayList<>();
    private static Map<ResponseEnum, List<URL>> MAP;

    @GetMapping(EVENT_CONTROLLER_SAMPLE_GET_MAPPING)
    public String sample() {
        ZonedDateTime zdt = ZonedDateTime.now(ZoneId.of("America/New_York"));
        String value = String.format("sample endpoint was hit at %s", zdt);
        log.info(value);
        return value;
    }

    @PostMapping(EVENT_CONTROLLER_CONSOLIDATE_POST_MAPPING)
    public void consolidate(@RequestParam String location) {
        File file = new File(location);
        ConsolidateUtility.consolidate(file);
    }

    @PostMapping(EVENT_CONTROLLER_DOWNLOAD_LINKS_POST_MAPPING)
    public String downloadLinks(@RequestParam String location, @RequestBody URL[] body) {
        MAP = new HashMap<>();
        File directory = new File(location);
        List<URL> urls = Arrays.asList(body);
        ZonedDateTime START = ZonedDateTime.now();
        String response = downloadLinks(directory, urls);
        ZonedDateTime END = ZonedDateTime.now();
        log.info("DOWNLOAD COMPLETED IN {} SECONDS", ChronoUnit.SECONDS.between(START, END));
        return response.replaceAll(BACK_SLASHES, EMPTY);
    }

    @SuppressWarnings("unchecked")
    private String downloadLinks(File directory, List<URL> urls) {
        if (urls.isEmpty()) {
            JSONObject jsonObject = new JSONObject();
            JSONArray jsonArray;
            for (ResponseEnum responseEnum: MAP.keySet()) {
                List<URL> value = MAP.get(responseEnum);
                jsonArray = new JSONArray();
                jsonArray.addAll(value.stream().map(URL::toString).toList());
                jsonObject.put(responseEnum.toString(), jsonArray);
            }
            return jsonObject.toJSONString();
        } else {
            List<URL> parsedURLs, newURLs = new ArrayList<>();
            WebsiteEnum websiteEnum;
            ResponseEnum responseEnum;
            Response response;
            log.info("# of urls: {}", urls.size());
            for (URL url : urls) {
                websiteEnum = WebsiteEnum.getByURL(url);
                if (websiteEnum == null) {
                    responseEnum = UNKNOWN;
                } else {
                    parsedURLs = websiteEnum.getURLs(url, client);
                    if (parsedURLs == null) {
                        try {
                            response = OkHttpUtility.sendRequest(url, client);
                            log.info("RESPONSE: {}", response);
//                            String contentType = response.header("Content-Type");
                            response.close();
                            responseEnum = DOWNLOAD;
                        } catch (Exception e) {
                            log.error("unable to get content type from response for url {}", url, e);
                            responseEnum = FAILURE;
                        }
                    } else if (parsedURLs.isEmpty()) {
                        responseEnum = FAILURE;
                    } else {
                        newURLs.addAll(parsedURLs);
                        responseEnum = SUCCESSFUL;
                    }
                }
                if (responseEnum != SUCCESSFUL) {
                    List<URL> value = MAP.get(responseEnum);
                    if (value == null) {
                        value = Collections.singletonList(url);
                    } else {
                        value = new ArrayList<>(value);
                        value.add(url);
                    }
                    MAP.put(responseEnum, value);
                }
            }
            return downloadLinks(directory, newURLs);
        }
    }

    private List<URL> getURLs(URL url, ResponseEnum responseEnum) {
        List<URL> value = MAP.get(responseEnum);
        if (value == null) {
            value = Collections.singletonList(url);
        } else {
            value = new ArrayList<>(value);
            value.add(url);
        }
        return value;
    }

}

//                if (websiteEnum == null) {
//                    responseEnum = UNKNOWN;
//                } else {
//                    newURLs = websiteEnum.getURLs(url, client);
//                    if (newURLs == null) {
//                        responseEnum = null;
//                    } else if (newURLs.isEmpty()) {
//                        responseEnum = FAILURE;
//                    } else {
//                        responseEnum = SUCCESSFUL;
//                    }
//                }
//                if (responseEnum == null) {
//                    log.info("DOWNLOAD: {}", url);
//                } else {
//                    RESPONSES.add(String.format("%-20s%s", responseEnum, url));
//                }