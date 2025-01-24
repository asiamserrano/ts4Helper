package ts4.helper.TS4Downloader.consumers;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import okhttp3.OkHttpClient;
import okhttp3.Response;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.stereotype.Service;
import ts4.helper.TS4Downloader.constructors.DomainPath;
import ts4.helper.TS4Downloader.enums.ExtensionEnum;
import ts4.helper.TS4Downloader.enums.TopicEnum;
import ts4.helper.TS4Downloader.enums.WebsiteEnum;
import ts4.helper.TS4Downloader.models.WebsiteModel;
import ts4.helper.TS4Downloader.utilities.OkHttpUtility;
import ts4.helper.TS4Downloader.utilities.StringUtility;
import ts4.helper.TS4Downloader.utilities.TopicUtility;
import ts4.helper.TS4Downloader.utilities.URLUtility;

import java.net.URL;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.time.ZonedDateTime;
import java.util.Map;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.stream.Collectors;

import static ts4.helper.TS4Downloader.constants.StringConstants.AMPERSAND;
import static ts4.helper.TS4Downloader.constants.StringConstants.BACK_SLASHES;
import static ts4.helper.TS4Downloader.constants.StringConstants.COMMA;
import static ts4.helper.TS4Downloader.constants.StringConstants.EMPTY;
import static ts4.helper.TS4Downloader.constants.StringConstants.SINGLE_QUOTE;

@Service
@Slf4j
@EnableScheduling
@AllArgsConstructor
public class MyTopicConsumer {

    private ExecutorService consumer;
    private KafkaTemplate<String, String> kafkaTemplate;

    private OkHttpClient client;

    private static ZonedDateTime START = null;

    @KafkaListener(topics = "my-topic", groupId = "consumer-group")
    public void listenGroupFoo(String message) {
        consumer.execute(() -> {
            log.info("received message: {}", message);
            String input = message.strip();
            if (!input.isEmpty()) {
                JSONObject jsonObject = (JSONObject) JSONValue.parse(input);
                WebsiteModel websiteModel = new WebsiteModel(jsonObject);
                URL url = websiteModel.url;
                log.info("downloading link {}", url);
                WebsiteEnum websiteEnum = WebsiteEnum.getByURL(url);
                if (websiteEnum == null) {
                    log.info("website is null");
                } else {
                    parse(websiteEnum, websiteModel);
                }
            }
        });
    }

    @FunctionalInterface
    private interface ParseFunction {
        List<WebsiteModel> parse(WebsiteModel model, String content);
    }

    private WebsiteModel parse(URL url, String string, WebsiteModel model) {
        WebsiteModel websiteModel = new WebsiteModel(url, string, model);
        send(websiteModel);
        return websiteModel;
    }

    private boolean isContentInvalid(String content) {
        return content.contains("Just a moment...");
    }

    private void download(WebsiteModel websiteModel) {
        log.info("sending to download topic: {}", websiteModel.url);
        TopicUtility.send(websiteModel, TopicEnum.MY_SECOND_TOPIC, kafkaTemplate);
    }

    private void send(WebsiteModel websiteModel) {
        log.info("sending to consumer topic: {}", websiteModel.url);
        TopicUtility.send(websiteModel, TopicEnum.MY_TOPIC, kafkaTemplate);
    }

    private void parse(WebsiteEnum websiteEnum, WebsiteModel websiteModel) {
        switch (websiteEnum) {
            case PATREON_POSTS: {
                String content = OkHttpUtility.getContent(websiteModel, client);
                String folder = StringUtility.getStringBetweenRegex(content, "<title>", "</title>");
                WebsiteModel newWebsiteModel = new WebsiteModel(websiteModel.url, folder, null);
                StringUtility.getSetBetweenRegex(content, "{\"attributes\":{\"name\":\"", "\"},")
                        .parallelStream().forEach(s -> {
                            String filename = s.split(",")[0];
                            String url_string = s.split("url:")[1].replace("\\u0026i", "&i");
                            send(new WebsiteModel(url_string, filename, newWebsiteModel));
                        });
                break;
            }
            case SIMS_FINDS_DOWNLOADS: {
                String content = OkHttpUtility.getContent(websiteModel, client);
                String name = StringUtility.getStringBetweenRegex(content, "<title id=\"title\">", "</title>");
                WebsiteModel newWebsiteModel = new WebsiteModel(websiteModel.url, name, null);
                String continue_string = StringUtility.getStringBetweenRegex(content, "data-continue=\"", "\"");
                WebsiteModel singleton = new WebsiteModel(continue_string, name, newWebsiteModel);
                send(singleton);
                break;
            }
            case SIMS_FINDS_CONTINUE: {
                String content = OkHttpUtility.getContent(websiteModel, client);
                String flid = StringUtility.getStringBetweenRegex(content, "data-at8r136r7=\"", SINGLE_QUOTE);
                String pass = StringUtility.getStringBetweenRegex(content, "data-passe=\"", SINGLE_QUOTE);
                String[] info = StringUtility.getStringBetweenRegex(content, "data-at5t768r9=\"", SINGLE_QUOTE)
                        .split(COMMA);
                Map<String, String> map = new HashMap<>() {{
                    put("cid", info[0]);
                    put("key", info[1]);
                    put("version", info[3]);
                    put("pass", pass);
                    put("flid", flid);
                }};
                List<String> list = map.entrySet().parallelStream()
                        .map(e -> String.format("%s=%s", e.getKey(), e.getValue()))
                        .collect(Collectors.toList());
                String downloadURLString = "https://click.simsfinds.com/download?" + String.join(AMPERSAND, list);
                WebsiteModel singleton = new WebsiteModel(downloadURLString, websiteModel.name, websiteModel);
                send(singleton);
                break;
            }
            case SIMS_FINDS_DOWNLOAD: {
                URL newURL = websiteModel.url;
                if (newURL.toString().contains("flid=0")) {
                    String content = OkHttpUtility.getContent(newURL, client);
                    URL externalURL = URLUtility.createURL(StringUtility.getStringBetweenRegex(content, "<title>", "</title"));
                    WebsiteModel singleton = new WebsiteModel(externalURL, EMPTY, websiteModel);
                    send(singleton);
                } else {
                    Response response = OkHttpUtility.sendRequest(newURL, client);
                    String filename = websiteModel.name + ExtensionEnum.getExtension(response);
                    WebsiteModel newWebsiteModel = new WebsiteModel(newURL, filename, websiteModel.previous);
                    download(newWebsiteModel);
                }
                break;
            }
            case CURSE_FORGE_CAS: {
                String content = OkHttpUtility.getContent(websiteModel, client);
                if (isContentInvalid(content)) {
                    log.error("curse forge cookie is invalid. cannot parse {}", websiteModel.url);
                } else {
                    String projectId = StringUtility.getStringBetweenRegex(content, "Project ID</dt><dd>", "</dd>");
                    String info = StringUtility.getStringBetweenRegex(content, "\"mainFile\":{\"id\":", ",\"displayName\"");
                    String id = info.split(",")[0];
                    String filename = info.split("fileName:")[1];
                    String urlString = String.format("https://www.curseforge.com/api/v1/mods/%s/files/%s/download", projectId, id);
                    URL newURL = URLUtility.createURL(urlString);
                    WebsiteModel singleton = new WebsiteModel(newURL, filename, websiteModel);
                    send(singleton);
                    break;
                }
            }
            case CURSE_FORGE_CREATORS: {
                ParseFunction parse = (model, content) ->
                        StringUtility.getSetBetweenRegex(content, "\"downloadLink\":\"", SINGLE_QUOTE)
                                .parallelStream()
                                .map(str -> {
                                    String string = str.replaceAll(BACK_SLASHES, EMPTY);
                                    String[] parts = string.split("/");
                                    String name = URLDecoder.decode(parts[parts.length - 1], StandardCharsets.UTF_8);
                                    URL newURL = URLUtility.createURL(string);
                                    return parse(newURL, name, model);
                                })
                                .toList();
                parse(websiteModel, "projectsPage=", parse);
                break;
            }
            case CURSE_FORGE_MEMBERS: {
                ParseFunction parse = (model, content) ->
                        StringUtility.getSetBetweenRegex(content, "<a class=\" download-cta btn-cta\" href=\"/", "/download")
                                .parallelStream()
                                .map(str -> URLUtility.createURL(WebsiteEnum.CURSE_FORGE_CAS.getHttpUrl() + str.replace(DomainPath.S4_CAS.value, EMPTY)))
                                .map(newURL -> parse(newURL, EMPTY, model))
                                .toList();
                parse(websiteModel, "page=", parse);
                break;
            }
            case PATREON_FILE, CURSE_FORGE_API, CURSE_FORGE_CDN: {
                download(websiteModel);
                break;
            }
            default: {
                log.info("unknown url: {}", websiteModel.url);
                break;
            }
        }
    }

    private void parse(WebsiteModel websiteModel, String marker, ParseFunction function) {
        URL url = websiteModel.url;
        log.info("parsing search page: {}", url);
        String content = OkHttpUtility.getContent(url, client);
        if (isContentInvalid(content)) {
            log.error("curse forge cookie is invalid. could not parse {}", url);
        } else {
            if (function.parse(websiteModel, content).isEmpty()) {
                log.info("no more results");
            } else {
                String url_string = url.toString();
                try {
                    String page_string = StringUtility.getStringBetweenRegex(url_string, marker, AMPERSAND);
                    int page = Integer.parseInt(page_string);
                    int next = page + 1;
                    String newWebsiteURL = url_string.replace(marker + page, marker + next);
                    WebsiteModel newWebsiteModel = new WebsiteModel(newWebsiteURL, EMPTY, null);
                    send(newWebsiteModel);
                } catch (Exception ex) {
                    log.error("unable to search url: {}", url_string);
                }
            }
        }
    }

//    @Scheduled(fixedDelay = 1000)
//    public void scheduleFixedRateWithInitialDelayTask() {
//        int es = ((ThreadPoolExecutor) executorService).getActiveCount();
//        int d = ((ThreadPoolExecutor) downloader).getActiveCount();
//        if (START == null) {
//            if (es > 0) {
//                START = ZonedDateTime.now();
//            }
//        } else {
//            if (es == 0 && d == 0) {
//                log.info("DOWNLOAD COMPLETED IN {} SECONDS", ChronoUnit.SECONDS.between(START, ZonedDateTime.now()));
//                START = null;
//            }
//        }
//    }

}
