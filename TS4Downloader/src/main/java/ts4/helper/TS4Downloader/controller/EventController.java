package ts4.helper.TS4Downloader.controller;

import okhttp3.Call;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.*;

import java.time.ZoneId;
import java.time.ZonedDateTime;

import org.slf4j.Logger;
import ts4.helper.TS4Downloader.constants.WebsiteEnum;
import ts4.helper.TS4Downloader.downloader.CurseForge;

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

    @PostMapping("/request")
    public String request(@RequestBody String url) {
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
            switch (websiteEnum) {
                case null -> {
                    return content;
                }
                case CURSE_FORGE -> {
                    return curseForge(content);
                }
            }
        } catch (Exception ex) {
            log.error("could not make call");
            return null;
        }
    }

    private String curseForge(String content) throws Exception {
        if (!content.contains("Just a moment...")) {
            if (CurseForge.download(content, location)) {
                return "SUCCESSFUL";
            }
        }
        log.error(content);
        return "FAILURE";
    }

}
