package ts4.helper.TS4Downloader.controllers;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.checkerframework.checker.units.qual.A;
import org.springframework.beans.factory.annotation.Autowired;
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
import java.util.ArrayList;
import java.util.List;

import ts4.helper.TS4Downloader.downloaders.SimsFindsDownloader;
import ts4.helper.TS4Downloader.enums.WebsiteEnum;
import ts4.helper.TS4Downloader.downloaders.CurseForgeDownloader;
import ts4.helper.TS4Downloader.downloaders.PatreonDownloader;
import ts4.helper.TS4Downloader.models.DownloadResponse;
import ts4.helper.TS4Downloader.utilities.ConsolidateUtility;
import ts4.helper.TS4Downloader.utilities.FileUtility;
import ts4.helper.TS4Downloader.utilities.URLUtility;

import static ts4.helper.TS4Downloader.constants.StringConstants.NEW_LINE;

import static ts4.helper.TS4Downloader.constants.ControllerConstants.EVENT_CONTROLLER_REQUEST_MAPPING;
import static ts4.helper.TS4Downloader.constants.ControllerConstants.EVENT_CONTROLLER_SAMPLE_GET_MAPPING;
import static ts4.helper.TS4Downloader.constants.ControllerConstants.EVENT_CONTROLLER_DOWNLOAD_LINKS_POST_MAPPING;
import static ts4.helper.TS4Downloader.constants.ControllerConstants.EVENT_CONTROLLER_CONSOLIDATE_POST_MAPPING;

import static ts4.helper.TS4Downloader.enums.ResponseEnum.SUCCESSFUL;


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

    private File nonDownloadedLinksFile;

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
    public String downloadLinks(@RequestParam String location, @RequestBody String body) {
        File directory = new File(location);
        String response;
        if (FileUtility.createDirectory(directory)) {
            String[] strings = body.split(NEW_LINE);
            try {
                List<URL> urls = URLUtility.createURLs(strings);
                RESPONSES = downloadLinks(urls, directory);
            } catch (Exception ex) {
                log.error("could not create urls", ex);
            }
            ConsolidateUtility.consolidate(directory);
            FileUtility.deleteNonPackageFiles(directory);
            response = String.join(NEW_LINE, RESPONSES);
        } else {
            log.error("location path is invalid: {}", location);
            response = "unable to download links to location: " + location;
        }
        RETRIES = 0;
        RESPONSES = new ArrayList<>();
        return response;
    }

    private List<String> downloadLinks(List<URL> urls, File directory) throws Exception {
        if (urls.isEmpty()) {
            return RESPONSES;
        } else {
           if (RETRIES < 3) {
               List<URL> list = new ArrayList<>();
               URL newURL;
               DownloadResponse response;
               WebsiteEnum websiteEnum;
               for (URL url: urls) {
                   log.info("downloading url {}", url);
                   websiteEnum = WebsiteEnum.contains(url);
                   response = getResponse(websiteEnum, url, directory);
                   if (response.responseEnum.equals(SUCCESSFUL)) {
                       log.info(response.toString());
                   } else {
                       newURL = response.url;
                       websiteEnum = WebsiteEnum.contains(newURL);
                       if (websiteEnum == null) {
                           writeNonDownloadedLink(newURL);
                       } else {
                           list.add(newURL);
                       }
                   }
               }
               RETRIES++;
               return downloadLinks(list, directory);
           } else {
               for (URL url : urls) writeNonDownloadedLink(url);
               return RESPONSES;
           }
        }
    }

    private void writeNonDownloadedLink(URL url) throws Exception {
        String urlString = url.toString();
        FileUtility.writeToFile(nonDownloadedLinksFile, urlString, true);
        RESPONSES.add(urlString);
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
