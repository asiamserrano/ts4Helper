package ts4.helper.TS4Downloader.controllers;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import okhttp3.OkHttpClient;
import okhttp3.Response;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestBody;

import java.io.File;
import java.io.FileWriter;
import java.net.URL;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;

import ts4.helper.TS4Downloader.downloaders.SimsFindsDownloader;
import ts4.helper.TS4Downloader.enums.ResponseEnum;
import ts4.helper.TS4Downloader.enums.WebsiteEnum;
import ts4.helper.TS4Downloader.downloaders.CurseForgeDownloader;
import ts4.helper.TS4Downloader.downloaders.PatreonDownloader;
import ts4.helper.TS4Downloader.models.DownloadResponse;
import ts4.helper.TS4Downloader.utilities.ConsolidateUtility;
import ts4.helper.TS4Downloader.utilities.FileUtility;
import ts4.helper.TS4Downloader.utilities.OkHttpUtility;
import ts4.helper.TS4Downloader.utilities.URLUtility;

import static ts4.helper.TS4Downloader.constants.StringConstants.NEW_LINE;

import static ts4.helper.TS4Downloader.constants.ControllerConstants.EVENT_CONTROLLER_REQUEST_MAPPING;
import static ts4.helper.TS4Downloader.constants.ControllerConstants.EVENT_CONTROLLER_SAMPLE_GET_MAPPING;
import static ts4.helper.TS4Downloader.constants.ControllerConstants.EVENT_CONTROLLER_DOWNLOAD_LINKS_POST_MAPPING;
import static ts4.helper.TS4Downloader.constants.ControllerConstants.EVENT_CONTROLLER_CONSOLIDATE_POST_MAPPING;
import static ts4.helper.TS4Downloader.constants.ControllerConstants.EVENT_CONTROLLER_CURSE_FORGE_COOKIE_STATUS_GET_MAPPING;

import static ts4.helper.TS4Downloader.enums.ResponseEnum.SUCCESSFUL;
import static ts4.helper.TS4Downloader.enums.ResponseEnum.FAILURE;
import static ts4.helper.TS4Downloader.enums.ResponseEnum.UNKNOWN;

import static ts4.helper.TS4Downloader.enums.WebsiteEnum.CURSE_FORGE;


@RestController
@RequestMapping(EVENT_CONTROLLER_REQUEST_MAPPING)
@Slf4j
@AllArgsConstructor
public class EventController {

    @Autowired
    private CurseForgeDownloader curseForgeDownloader;

    @Autowired
    private PatreonDownloader patreonDownloader;

    @Autowired
    private SimsFindsDownloader simsFindsDownloader;

    private OkHttpClient client;
    private File nonDownloadedLinksFile;

    @GetMapping(EVENT_CONTROLLER_SAMPLE_GET_MAPPING)
    public String sample() {
        ZonedDateTime zdt = ZonedDateTime.now(ZoneId.of("America/New_York"));
        String value = String.format("sample endpoint was hit at %s", zdt);
        log.info(value);
        return value;
    }

    @GetMapping(EVENT_CONTROLLER_CURSE_FORGE_COOKIE_STATUS_GET_MAPPING)
    public String curseForgeCookieStatus() {
        ResponseEnum responseEnum;
        try {
            Response response = OkHttpUtility.sendRequest(CURSE_FORGE.httpUrl, client);
            boolean bool = response.isSuccessful();
            response.close();
            responseEnum = bool ? SUCCESSFUL : FAILURE;
            log.info("curse forge cookie is {}", bool ? "active" : "inactive");
            return responseEnum.toString();
        } catch (Exception e) {
            log.error("cannot check status of curseforge cookie. Exception: {} ", e.getMessage());
            responseEnum = UNKNOWN;
        }
        return responseEnum.toString();
    }

    @PostMapping(EVENT_CONTROLLER_CONSOLIDATE_POST_MAPPING)
    public void consolidate(@RequestParam String location) {
        File file = new File(location);
        ConsolidateUtility.consolidate(file);
    }

    @PostMapping(EVENT_CONTROLLER_DOWNLOAD_LINKS_POST_MAPPING)
    public String downloadLinks(@RequestParam String location, @RequestBody String body) {
        File directory = new File(location);
        if (FileUtility.createDirectory(directory)) {
            String[] strings = body.split(NEW_LINE);
            List<String> responses = new ArrayList<>();
            try {
                List<URL> urls = URLUtility.createURLs(strings);
                responses = downloadLinks(urls, directory, responses);
            } catch (Exception ex) {
                log.error("could not create urls {}", body, ex);
            }
            ConsolidateUtility.consolidate(directory);
            FileUtility.deleteNonPackageFiles(directory);
            return String.join(NEW_LINE, responses);
        } else {
            log.error("location path is invalid: {}", location);
            return "unable to download links to location: " + location;
        }
    }

    private List<String> downloadLinks(List<URL> urls, File directory, List<String> responses) throws Exception {
        if (urls.isEmpty()) {
            return responses;
        } else {
            List<URL> list = new ArrayList<>();
            URL newURL;
            DownloadResponse response;
            WebsiteEnum websiteEnum;
            String urlString;
            for (URL url: urls) {
                websiteEnum = WebsiteEnum.contains(url);
                response = getResponse(websiteEnum, url, directory);
                if (response.responseEnum.equals(SUCCESSFUL)) {
                    log.info(response.toString());
                } else {
                    newURL = response.url;
                    websiteEnum = WebsiteEnum.contains(newURL);
                    if (websiteEnum == null) {
                        urlString = newURL.toString();
                        FileUtility.writeToFile(nonDownloadedLinksFile, urlString, true);
                        responses.add(urlString);
                    } else {
                        list.add(newURL);
                    }
                }
            }
            return downloadLinks(list, directory, responses);
        }
    }

    private DownloadResponse getResponse(WebsiteEnum websiteEnum, URL url, File starting_directory) throws Exception {
        DownloadResponse defaultResponse = new DownloadResponse(url);
        if (websiteEnum == null) {
            return defaultResponse;
        } else {
            switch (websiteEnum) {
                case CURSE_FORGE -> {
                    return curseForgeDownloader.download(url, starting_directory);
                }
                case PATREON -> {
                    return patreonDownloader.download(url, starting_directory);
                }
                case SIMS_FINDS -> {
                    return simsFindsDownloader.download(url, starting_directory);
                }
                default -> {
                    return defaultResponse;
                }
            }
        }
    }

}
