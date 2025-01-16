package ts4.helper.TS4Downloader.controllers;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import okhttp3.OkHttpClient;
import org.checkerframework.checker.units.qual.A;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestBody;

import java.io.File;
import java.net.URL;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Set;
import java.util.HashSet;
import java.util.Map;
import java.util.HashMap;
import java.util.List;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;

import ts4.helper.TS4Downloader.enums.ResponseEnum;
import ts4.helper.TS4Downloader.enums.WebsiteEnum;
import ts4.helper.TS4Downloader.models.DownloadResponse;
import ts4.helper.TS4Downloader.utilities.ConsolidateUtility;

import static ts4.helper.TS4Downloader.constants.ControllerConstants.EVENT_CONTROLLER_REQUEST_MAPPING;
import static ts4.helper.TS4Downloader.constants.ControllerConstants.EVENT_CONTROLLER_SAMPLE_GET_MAPPING;
import static ts4.helper.TS4Downloader.constants.ControllerConstants.EVENT_CONTROLLER_DOWNLOAD_LINKS_POST_MAPPING;
import static ts4.helper.TS4Downloader.constants.ControllerConstants.EVENT_CONTROLLER_CONSOLIDATE_POST_MAPPING;

import static ts4.helper.TS4Downloader.constants.StringConstants.BACK_SLASHES;
import static ts4.helper.TS4Downloader.constants.StringConstants.EMPTY;

import static ts4.helper.TS4Downloader.enums.ResponseEnum.SUCCESSFUL;
import static ts4.helper.TS4Downloader.enums.ResponseEnum.FAILURE;
import static ts4.helper.TS4Downloader.enums.ResponseEnum.DOWNLOAD;
import static ts4.helper.TS4Downloader.enums.ResponseEnum.UNKNOWN;

@RestController
@RequestMapping(EVENT_CONTROLLER_REQUEST_MAPPING)
@Slf4j
@AllArgsConstructor
public class EventController {

    private File nonDownloadedLinksFile;
    private OkHttpClient client;
    private ExecutorService executorService;

    private static Map<ResponseEnum, Set<String>> MAP;

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
        String response = downloadLinks(directory, urls, 1);
        ZonedDateTime END = ZonedDateTime.now();
        log.info("DOWNLOAD COMPLETED IN {} SECONDS", ChronoUnit.SECONDS.between(START, END));
        return response.replaceAll(BACK_SLASHES, EMPTY);
    }

    @SuppressWarnings("unchecked")
    private String downloadLinks(File directory, List<URL> urls, int iteration) {
        if (urls.isEmpty()) {
            JSONObject jsonObject = new JSONObject();
            MAP.keySet().parallelStream().forEach(responseEnum -> {
                Set<String> value = MAP.get(responseEnum);
                JSONArray jsonArray = new JSONArray();
                jsonArray.addAll(value);
                jsonObject.put(responseEnum.toString(), jsonArray);
            });
            return jsonObject.toJSONString();
        } else {
            log.info("iteration: {} | # of urls: {}", iteration, urls.size());
            List <URL> newURLs = getNewURLs(urls);
            return downloadLinks(directory, newURLs, iteration + 1);
        }
    }

    private List<URL> getNewURLs(List<URL> urls) {
        List <URL> newURLs = new ArrayList<>();
        try {
            List<Future<?>> futures = new ArrayList<>(urls.stream().map(url ->
                    executorService.submit((Callable<Void>) () -> {
                        log.info(url.toString());
                        newURLs.addAll(getNewURLs(url));
                        return null;
                    })).toList());
            for(Future<?> f: futures) { f.get(); }
        } catch (Exception e) {
            log.error("Exception while downloading links", e);
        }
        return newURLs;
    }

    private List<URL> getNewURLs(URL url) {
        WebsiteEnum websiteEnum = WebsiteEnum.getByURL(url);
        DownloadResponse response;
        if (websiteEnum == null) {
            response = new DownloadResponse(UNKNOWN);
        } else {
            response = websiteEnum.getURLs(url, client);
        }
        return getNewURLs(url, response);
    }

    private List<URL> getNewURLs(URL url, DownloadResponse response ) {
        ResponseEnum responseEnum = response.responseEnum;
        if (responseEnum == SUCCESSFUL) {
            return new ArrayList<>(response.urls);
        } else {
            Set<String> set = getURLs(url, responseEnum);
            MAP.put(responseEnum, set);
            return new ArrayList<>();
        }
    }

    private Set<String> getURLs(URL url, ResponseEnum responseEnum) {
        Set<String> value = MAP.get(responseEnum);
        if (value == null) {
            value = Collections.singleton(url.toString());
        } else {
            value = new HashSet<>(value);
            value.add(url.toString());
        }
        return value;
    }

//    private void printMap() {
//        for (ResponseEnum responseEnum: MAP.keySet()) {
//            MAP.get(responseEnum).forEach(this::printNestedURL);
//        }
//    }
//
//    private void printNestedURL(NestedURL nestedURL) {
//        printNestedURL(nestedURL, EMPTY);
//    }
//
//    private void printNestedURL(NestedURL nestedURL, String string) {
//        if (nestedURL == null) {
//            System.out.println(string);
//        } else {
//            String url = nestedURL.url.toString();
//            NestedURL previous = nestedURL.previous;
//            if (string.isEmpty()) {
//                printNestedURL(previous, url);
//            } else {
//                printNestedURL(previous, url + " -> " + string);
//            }
//        }
//    }

}