package ts4.helper.TS4Downloader.controller;

import okhttp3.Call;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import ts4.helper.TS4Downloader.constants.WebsiteEnum;
import ts4.helper.TS4Downloader.downloader.CurseForgeDownloader;
import ts4.helper.TS4Downloader.downloader.PatreonDownloader;

import static ts4.helper.TS4Downloader.constants.StringConstants.NEW_LINE;

@RestController
@RequestMapping("/event")
public class EventController {

    private static final Logger log = LoggerFactory.getLogger(EventController.class);

    private static final String location = "/Users/asiaserrano/ChromeDownloads";

    private final OkHttpClient client;

    public EventController(final OkHttpClient client) {
        this.client = client;
    }

    @GetMapping("/sample")
    public String sample() {
        ZonedDateTime zdt = ZonedDateTime.now(ZoneId.of("America/New_York"));
        String value = String.format("sample endpoint was hit at %s", zdt);
        log.info(value);
        return value;
    }

    @PostMapping("/downloadLinks")
    public String downloadLinks(@RequestBody String body) {
        String[] urls = body.split(NEW_LINE);
        List<String> responses = new ArrayList<>();
        for (String url : urls) {
            responses.add(downloadLink(url));
        }
        return String.join(NEW_LINE, responses);
    }

    private String downloadLink(String url) {
        Request request = new Request.Builder()
                .url(url)
                .header("User-Agent", "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_15_7) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/131.0.0.0 Safari/537.36")
                .build();
        Call call = client.newCall(request);
        return download(call, url);
    }

    private String download(Call call, String url) {
        try(Response response = call.execute()) {
            String content = response.body().string();
            WebsiteEnum websiteEnum = WebsiteEnum.contains(url);
            boolean result = getResponse(websiteEnum, content);
            return result ? getSuccessfulMessage(url) : content;
        } catch (Exception ex) {
            log.error("could not make call", ex);
            throw new RuntimeException();
        }
    }

    private String getSuccessfulMessage(String url) {
        return "COMPLETED: " + url;
    }

    private boolean getResponse(WebsiteEnum websiteEnum, String content) throws Exception {
        if (websiteEnum == null) {
            return false;
        } else {
            switch (websiteEnum) {
                case CURSE_FORGE -> {
                    return curseForge(content);
                }
                case PATREON -> {
                    return patreon(content);
                }
                default -> {
                    return false;
                }
            }
        }
    }

    private boolean curseForge(String content) throws Exception {
        if (!content.contains("Just a moment...")) {
            if (CurseForgeDownloader.download(content, location)) {
                return true;
            }
        }
        log.error(content);
        return false;
    }

    private boolean patreon(String content) throws Exception {
        if (PatreonDownloader.download(content, location)) {
            return true;
        } else {
            log.error(content);
            return false;
        }
    }

}
