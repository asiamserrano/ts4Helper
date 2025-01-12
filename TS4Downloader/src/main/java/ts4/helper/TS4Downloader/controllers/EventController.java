package ts4.helper.TS4Downloader.controllers;

import lombok.extern.slf4j.Slf4j;
import okhttp3.Call;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestBody;

import java.io.File;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;

import ts4.helper.TS4Downloader.downloaders.SimsFindsDownloader;
import ts4.helper.TS4Downloader.enums.WebsiteEnum;
import ts4.helper.TS4Downloader.downloaders.CurseForgeDownloader;
import ts4.helper.TS4Downloader.downloaders.PatreonDownloader;
import ts4.helper.TS4Downloader.utilities.FileUtility;
import ts4.helper.TS4Downloader.utilities.OkHttpUtility;
import ts4.helper.TS4Downloader.utilities.URLUtility;

import static ts4.helper.TS4Downloader.constants.StringConstants.NEW_LINE;

import static ts4.helper.TS4Downloader.constants.ControllerConstants.EVENT_CONTROLLER_REQUEST_MAPPING;
import static ts4.helper.TS4Downloader.constants.ControllerConstants.EVENT_CONTROLLER_SAMPLE_GET_MAPPING;
import static ts4.helper.TS4Downloader.constants.ControllerConstants.EVENT_CONTROLLER_DOWNLOAD_LINKS_GET_MAPPING;

@RestController
@RequestMapping(EVENT_CONTROLLER_REQUEST_MAPPING)
@Slf4j
public class EventController {

    @Autowired
    private CurseForgeDownloader curseForgeDownloader;

    @Autowired
    private PatreonDownloader patreonDownloader;

    @Autowired
    private SimsFindsDownloader simsFindsDownloader;

    private final OkHttpClient client;

    public EventController(final OkHttpClient client) {
        this.client = client;
    }

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
            for (String url : urls) {
                try(Response response = OkHttpUtility.sendRequest(url, client)) {
                    String content = response.body().string();
                    WebsiteEnum websiteEnum = WebsiteEnum.contains(url);
                    boolean result = getResponse(websiteEnum, content, location);
                    responses.add(result ? getSuccessfulMessage(url) : content);
                } catch (Exception ex) {
                    log.error("could not make call", ex);
                }
            }
            return String.join(NEW_LINE, responses);
        } else {
            log.error("location path is invalid: {}", location);
            return null;
        }
    }

    private String getSuccessfulMessage(String url) {
        return "COMPLETED: " + url;
    }

    private boolean getResponse(WebsiteEnum websiteEnum, String content, String location) throws Exception {
        if (websiteEnum == null) {
            return false;
        } else {
            switch (websiteEnum) {
                case CURSE_FORGE -> {
                    return curseForgeDownloader.download(content, location);
                }
                case PATREON -> {
                    return patreonDownloader.download(content, location);
                }
                case SIMS_FINDS -> {
                    return simsFindsDownloader.download(content, location);
                }
                default -> {
                    return false;
                }
            }
        }
    }

}
