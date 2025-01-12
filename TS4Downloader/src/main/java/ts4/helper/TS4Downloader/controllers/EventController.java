package ts4.helper.TS4Downloader.controllers;

import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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
import ts4.helper.TS4Downloader.utilities.FileUtility;
import ts4.helper.TS4Downloader.utilities.URLUtility;

import static ts4.helper.TS4Downloader.constants.StringConstants.NEW_LINE;

import static ts4.helper.TS4Downloader.constants.ControllerConstants.EVENT_CONTROLLER_REQUEST_MAPPING;
import static ts4.helper.TS4Downloader.constants.ControllerConstants.EVENT_CONTROLLER_SAMPLE_GET_MAPPING;
import static ts4.helper.TS4Downloader.constants.ControllerConstants.EVENT_CONTROLLER_DOWNLOAD_LINKS_GET_MAPPING;

@RestController
@RequestMapping(EVENT_CONTROLLER_REQUEST_MAPPING)
@Slf4j
@NoArgsConstructor
public class EventController {

    @Autowired
    private CurseForgeDownloader curseForgeDownloader;

    @Autowired
    private PatreonDownloader patreonDownloader;

    @Autowired
    private SimsFindsDownloader simsFindsDownloader;

    @GetMapping(EVENT_CONTROLLER_SAMPLE_GET_MAPPING)
    public String sample() {
        ZonedDateTime zdt = ZonedDateTime.now(ZoneId.of("America/New_York"));
        String value = String.format("sample endpoint was hit at %s", zdt);
        log.info(value);
        return value;
    }

    @PostMapping(EVENT_CONTROLLER_DOWNLOAD_LINKS_GET_MAPPING)
    public String downloadLinks(@RequestParam String location, @RequestBody String body) {
        File directory = new File(location);
        if (FileUtility.createDirectory(directory)) {
            String[] urls = body.split(NEW_LINE);
            List<String> responses = new ArrayList<>();
            for (String str : urls) {
                try {
                    URL url = URLUtility.createURL(str);
                    File starting_directory = new File(location);
                    WebsiteEnum websiteEnum = WebsiteEnum.contains(url);
                    DownloadResponse response = getResponse(websiteEnum, url, starting_directory);
                    String result = response.result;
                    log.info(result);
                    responses.add(result);
                } catch (Exception ex) {
                    log.error("could not make call for {}", str, ex);
                }
            }
            return String.join(NEW_LINE, responses);
        } else {
            log.error("location path is invalid: {}", location);
            return "unable to download links to location: " + location;
        }
    }

    private DownloadResponse getResponse(WebsiteEnum websiteEnum, URL url, File starting_directory) throws Exception {
        DownloadResponse defaultResponse = new DownloadResponse(false, url);
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
