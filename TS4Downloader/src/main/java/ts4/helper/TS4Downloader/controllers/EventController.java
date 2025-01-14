package ts4.helper.TS4Downloader.controllers;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import okhttp3.OkHttpClient;
import org.checkerframework.checker.units.qual.A;
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
import java.util.*;
import java.util.stream.Collectors;

import ts4.helper.TS4Downloader.enums.ResponseEnum;
import ts4.helper.TS4Downloader.enums.WebsiteEnum;
import ts4.helper.TS4Downloader.models.DownloadResponse;
import ts4.helper.TS4Downloader.utilities.*;

import static ts4.helper.TS4Downloader.constants.ControllerConstants.EVENT_CONTROLLER_REQUEST_MAPPING;
import static ts4.helper.TS4Downloader.constants.ControllerConstants.EVENT_CONTROLLER_SAMPLE_GET_MAPPING;
import static ts4.helper.TS4Downloader.constants.ControllerConstants.EVENT_CONTROLLER_DOWNLOAD_LINKS_POST_MAPPING;
import static ts4.helper.TS4Downloader.constants.ControllerConstants.EVENT_CONTROLLER_CONSOLIDATE_POST_MAPPING;
import static ts4.helper.TS4Downloader.constants.StringConstants.*;
import static ts4.helper.TS4Downloader.enums.ResponseEnum.*;


@RestController
@RequestMapping(EVENT_CONTROLLER_REQUEST_MAPPING)
@Slf4j
@AllArgsConstructor
public class EventController {

    private File nonDownloadedLinksFile;
    private OkHttpClient client;

    private static int RETRIES = 0;
    private static List<String> RESPONSES = new ArrayList<>();

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
        File directory = new File(location);
        List<URL> urls = Arrays.asList(body);
        return downloadLinks(directory, urls);


//        log.info("saving to: {}", directory);
//        String[] strings = body.split(NEW_LINE);
//        URL url;
//        ResponseEnum responseEnum;
//        Set<String> hosts = new HashSet<>();
//        for (String string: strings) {
//            try {
//                url = URLUtility.createURL(string);
//
////                log.info("host: {}", url.getHost());
//                hosts.add(url.getHost());
//                responseEnum = SUCCESSFUL;
//            } catch (Exception e) {
//                responseEnum = FAILURE;
//            }
//        }

//        if (FileUtility.createDirectory(directory)) {
//            String[] strings = body.split(NEW_LINE);
//            try {
//                List<URL> urls = URLUtility.createURLs(strings);
//                RESPONSES = downloadLinks(urls, directory);
//            } catch (Exception ex) {
//                log.error("could not create urls", ex);
//            }
//            ConsolidateUtility.consolidate(directory);
//            FileUtility.deleteNonPackageFiles(directory);
//            response = String.join(NEW_LINE, RESPONSES);
//        } else {
//            log.error("location path is invalid: {}", location);
//            response = "unable to download links to location: " + location;
//        }
//        RETRIES = 0;
//        RESPONSES = new ArrayList<>();
//        Collections.sort(RESPONSES);
//        String responses = String.join(NEW_LINE, RESPONSES);
//        RESPONSES = new ArrayList<>();
//        return responses;
    }

    private String downloadLinks(File directory, List<URL> urls) {
        if (urls.isEmpty()) {
            Collections.sort(RESPONSES);
            String responses = String.join(NEW_LINE, RESPONSES);
            RESPONSES = new ArrayList<>();
            return responses;
        } else {
            List<URL> newURLs = new ArrayList<>();
            WebsiteEnum websiteEnum;
            ResponseEnum responseEnum;
            for (URL url : urls) {
                websiteEnum = WebsiteEnum.getByURL(url);
                if (websiteEnum == null) {
                    responseEnum = UNKNOWN;
                } else {
                    switch (websiteEnum.domainEnum) {
                        case PATREON -> {
                            switch (websiteEnum) {
                                case PATREON_FILE -> {
                                    log.info("NEED TO DOWNLOAD: {}", url);
                                }
                                case PATREON_POSTS -> {
                                    try {
                                        String content = OkHttpUtility.getContent(url, client);
                                        Set<String> set = StringUtility.getSetBetweenRegex(content, "<a href=\"/file?", SINGLE_QUOTE);
                                        List<String> list = set.stream()
                                                .map(s -> "https://www.patreon.com/file?" + s.replace("amp;", EMPTY))
                                                .toList();
                                        if (list.isEmpty()) {
                                            log.error("patreon link is not valid: {}", url);
                                        } else {
                                            log.info("urls found for {}: [{}]", url, String.join(", ", list));
                                            for (String link : list) newURLs.add(URLUtility.createURL(link));
                                        }
                                    } catch (Exception e) {
                                        log.error("error when trying to download patreon link: {}", url, e);
                                    }
                                }
                                default -> log.error("unknown patreon website: {}", url);
                            }
                        }
                        case SIMS_FINDS -> {
                            switch (websiteEnum) {
                                case SIMS_FINDS_DOWNLOADS -> {
                                    try {
                                        String content = OkHttpUtility.getContent(url, client);
                                        String key = StringUtility.getStringBetweenRegex(content, "key=", SINGLE_QUOTE);
                                        URL newURL = URLUtility.createURL("https://www.simsfinds.com/continue?key=" + key);
                                        log.info("url found for {}: {}", url, newURL);
                                        newURLs.add(newURL);
                                    } catch (Exception e) {
                                        log.error("error when trying to download simsfinds downloads link: {}", url, e);
                                    }
                                }
                                case SIMS_FINDS_CONTINUE -> {
                                    try {
                                        String content = OkHttpUtility.getContent(url, client);
                                        String[] info = StringUtility.getStringBetweenRegex(content, "data-at5t768r9=\"", SINGLE_QUOTE).split(COMMA);
                                        String flid = StringUtility.getStringBetweenRegex(content, "data-at8r136r7=\"", SINGLE_QUOTE);
                                        String pass = StringUtility.getStringBetweenRegex(content, "data-passe=\"", SINGLE_QUOTE);

                                        Map<String, String> map = new HashMap<>();
                                        map.put("cid", info[0]);
                                        map.put("key", info[1]);
                                        map.put("version", info[3]);
                                        map.put("pass", pass);
                                        map.put("flid", flid);

                                        List<String> list = map.entrySet().stream()
                                                .map(e -> String.format("%s=%s", e.getKey(), e.getValue()))
                                                .collect(Collectors.toList());
                                        URL newURL = URLUtility.createURL("https://click.simsfinds.com/download?" + String.join(AMPERSAND, list));
                                        log.info("url found for {}: {}", url, newURL);
                                        newURLs.add(newURL);
                                    } catch (Exception e) {
                                        log.error("error when trying to download simsfinds continue link: {}", url, e);
                                    }
                                }
                                case SIMS_FINDS_DOWNLOAD -> {
                                    // NEED TO FIX
//                                    try {
//                                        if (url.toString().contains("flid=0")) {
//                                            String content = OkHttpUtility.getContent(url, client);
//                                            String external_url = StringUtility.getStringBetweenRegex(content, "<title>", "</title");
//                                            if (external_url.isEmpty()) {
//                                                log.error("unknown simsfinds downloads website: {}", url);
//                                            } else {
//                                                URL newURL = URLUtility.createURL(external_url);
//                                                log.info("url found for {}: {}", url, newURL);
//                                                newURLs.add(newURL);
//                                            }
//                                        } else {
//                                            log.info("NEED TO DOWNLOAD: {}", url);
//                                        }
//                                    } catch (Exception e) {
//                                        log.error("error when trying to download simsfinds download link: {}", url, e);
//                                    }
                                }
                                default -> log.error("unknown simsfinds website: {}", url);
                            }
                        }
                        default -> log.info("website: {}, url: {}", websiteEnum, url);
                    }
                    responseEnum = SUCCESSFUL;
                }
                RESPONSES.add(String.format("%-20s%s", responseEnum, url));
            }
            return downloadLinks(directory, newURLs);
        }
    }

//    private List<String> downloadLinks(List<URL> urls, File directory) throws Exception {
//        if (urls.isEmpty()) {
//            return RESPONSES;
//        } else {
//           if (RETRIES < 3) {
//               List<URL> list = new ArrayList<>();
//               URL newURL;
//               DownloadResponse response;
//               WebsiteEnum websiteEnum;
//               for (URL url: urls) {
//                   log.info("downloading url {}", url);
//                   websiteEnum = WebsiteEnum.contains(url);
//                   response = getResponse(websiteEnum, url, directory);
//                   if (response.responseEnum.equals(SUCCESSFUL)) {
//                       log.info(response.toString());
//                   } else {
//                       newURL = response.url;
//                       websiteEnum = WebsiteEnum.contains(newURL);
//                       if (websiteEnum == null) {
//                           writeNonDownloadedLink(newURL);
//                       } else {
//                           list.add(newURL);
//                       }
//                   }
//               }
//               RETRIES++;
//               return downloadLinks(list, directory);
//           } else {
//               for (URL url : urls) writeNonDownloadedLink(url);
//               return RESPONSES;
//           }
//        }
//    }
//
//    private void writeNonDownloadedLink(URL url) throws Exception {
//        String urlString = url.toString();
//        FileUtility.writeToFile(nonDownloadedLinksFile, urlString, true);
//        RESPONSES.add(urlString);
//    }

    private DownloadResponse getResponse(WebsiteEnum websiteEnum, URL url, File starting_directory) throws Exception {
        DownloadResponse defaultResponse = new DownloadResponse(url);
        return defaultResponse;
//        if (websiteEnum == null) {
//            return defaultResponse;
//        } else {
//            switch (websiteEnum) {
//                case CURSE_FORGE -> {
//                    return curseForgeDownloader.download(url, starting_directory);
//                }
//                case PATREON -> {
//                    return patreonDownloader.download(url, starting_directory);
//                }
//                case SIMS_FINDS -> {
//                    return simsFindsDownloader.download(url, starting_directory);
//                }
//                default -> {
//                    return defaultResponse;
//                }
//            }
//        }
    }

}
