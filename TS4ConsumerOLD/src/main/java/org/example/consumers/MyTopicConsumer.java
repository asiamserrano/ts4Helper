package org.example.consumers;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import okhttp3.OkHttpClient;
import okhttp3.Response;
import org.example.ts4package.classes.TS4ExecutorService;
import org.example.ts4package.constructors.DomainPath;
import org.example.ts4package.enums.ExtensionEnum;
import org.example.ts4package.enums.TopicEnum;
import org.example.ts4package.enums.WebsiteEnum;
import org.example.ts4package.models.MessageModel;
import org.example.ts4package.models.WebsiteModel;
import org.example.ts4package.utilities.OkHttpUtility;
import org.example.ts4package.utilities.StringUtility;
import org.example.ts4package.utilities.KafkaUtility;
import org.example.ts4package.utilities.URLUtility;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.stereotype.Service;

import java.net.URL;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static org.example.ts4package.constants.StringConstants.*;

@Service
@Slf4j
@EnableScheduling
@AllArgsConstructor
public class MyTopicConsumer {

    private TS4ExecutorService ts4ExecutorService;
    private KafkaTemplate<String, String> kafkaTemplate;
    private OkHttpClient client;

    @KafkaListener(topics = "${spring.kafka.template.default-topic}", groupId = "${spring.kafka.consumer.group-id}")
    public void listenGroupFoo(String message) {
        ts4ExecutorService.executorService.execute(() -> {
            log.info("received message: {}", message);
            String input = message.strip();
            if (!input.isEmpty()) {
                MessageModel messageModel = MessageModel.Builder.parse(message).build();
                URL url = messageModel.websiteModel.url;
                WebsiteEnum websiteEnum = WebsiteEnum.getByURL(url);
                if (websiteEnum == null) {
                    log.error("unknown link: {}", url);
                } else {
                    parse(websiteEnum, messageModel);
                }
            }
        });
    }

    @FunctionalInterface
    private interface ParseFunction {
        List<MessageModel> parse(MessageModel model, String content);
    }

    private MessageModel parse(URL url, String string, MessageModel model) {
        WebsiteModel wm = new WebsiteModel.Builder(url)
                .setName(string)
                .setPrevious(model.websiteModel)
                .build();
        MessageModel singleton = new MessageModel.Builder(model.directory, wm).build();
        send(singleton);
        return singleton;
    }

    private boolean isContentInvalid(String content) {
        return content.contains("Just a moment...");
    }

    private void download(MessageModel messageModel) {
        log.info("sending to download topic: {}", messageModel.websiteModel.url);
        KafkaUtility.send(messageModel, TopicEnum.MY_SECOND_TOPIC, kafkaTemplate);
    }

    private void send(MessageModel messageModel) {
        log.info("sending to consumer topic: {}", messageModel.websiteModel.url);
        KafkaUtility.send(messageModel, TopicEnum.MY_TOPIC, kafkaTemplate);
    }

    private void parse(WebsiteEnum websiteEnum, MessageModel messageModel) {
        WebsiteModel websiteModel = messageModel.websiteModel;
        switch (websiteEnum) {
            case PATREON_POSTS: {
                String content = OkHttpUtility.getContent(websiteModel, client);
                String folder = StringUtility.getStringBetweenRegex(content, "<title>", "</title>");
                WebsiteModel previous = new WebsiteModel.Builder(websiteModel.url).setName(folder).build();
                List<MessageModel> models = StringUtility.getSetBetweenRegex(content, "{\"attributes\":{\"name\":\"", "\"},")
                        .parallelStream().map(s -> {
                            String name = s.split(",")[0];
                            String string = s.split("url:")[1].replace("\\u0026i", "&i");
                            WebsiteModel wm = new WebsiteModel.Builder(string).setName(name).setPrevious(previous).build();
                            return new MessageModel.Builder(messageModel.directory, wm).build();
                        }).toList();
                if (models.isEmpty()) {
                    log.error("patreon link is invalid: {}", websiteModel.url);
                } else {
                    models.forEach(this::send);
                }
                break;
            }
            case SIMS_FINDS_DOWNLOADS: {
                String content = OkHttpUtility.getContent(websiteModel, client);
                String name = StringUtility.getStringBetweenRegex(content, "<title id=\"title\">", "</title>");
                WebsiteModel previous = new WebsiteModel.Builder(websiteModel.url).setName(name).build();
                String cont = StringUtility.getStringBetweenRegex(content, "data-continue=\"", "\"");
                WebsiteModel wm = new WebsiteModel.Builder(cont).setName(name).setPrevious(previous).build();
                MessageModel singleton = new MessageModel.Builder(messageModel.directory, wm).build();
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
                WebsiteModel wm = new WebsiteModel.Builder(downloadURLString)
                        .setName(websiteModel.name)
                        .setPrevious(websiteModel)
                        .build();
                MessageModel singleton = new MessageModel.Builder(messageModel.directory, wm).build();
                send(singleton);
                break;
            }
            case SIMS_FINDS_DOWNLOAD: {
                URL newURL = websiteModel.url;
                if (newURL.toString().contains("flid=0")) {
                    String content = OkHttpUtility.getContent(newURL, client);
                    String urlString = StringUtility.getStringBetweenRegex(content, "<title>", "</title");
                    WebsiteModel wm = new WebsiteModel.Builder(urlString)
                            .setPrevious(websiteModel)
                            .build();
                    MessageModel singleton = new MessageModel.Builder(messageModel.directory, wm).build();
                    send(singleton);
                } else {
                    Response response = OkHttpUtility.sendRequest(newURL, client);
                    String filename = websiteModel.name + ExtensionEnum.getExtension(response);
                    WebsiteModel wm = new WebsiteModel.Builder(newURL)
                            .setName(filename)
                            .setPrevious(websiteModel.previous)
                            .build();
                    MessageModel singleton = new MessageModel.Builder(messageModel.directory, wm).build();
                    download(singleton);
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
                    WebsiteModel wm = new WebsiteModel.Builder(urlString)
                            .setName(filename)
                            .setPrevious(websiteModel)
                            .build();
                    MessageModel singleton = new MessageModel.Builder(messageModel.directory, wm).build();
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
                                    URL newURL = URLUtility.createURLNoException(string);
                                    return parse(newURL, name, model);
                                })
                                .toList();
                parse(messageModel, "projectsPage=", parse);
                break;
            }
            case CURSE_FORGE_MEMBERS: {
                ParseFunction parse = (model, content) ->
                        StringUtility.getSetBetweenRegex(content, "<a class=\" download-cta btn-cta\" href=\"/", "/download")
                                .parallelStream()
                                .map(str -> URLUtility.createURLNoException(WebsiteEnum.CURSE_FORGE_CAS.getHttpUrl() + str.replace(DomainPath.S4_CAS.value, EMPTY)))
                                .map(newURL -> parse(newURL, EMPTY, model))
                                .toList();
                parse(messageModel, "page=", parse);
                break;
            }
            case PATREON_FILE, CURSE_FORGE_API, CURSE_FORGE_CDN: {
                download(messageModel);
                break;
            }
        }
    }

    private void parse(MessageModel messageModel, String marker, ParseFunction function) {
        WebsiteModel websiteModel = messageModel.websiteModel;
        URL url = websiteModel.url;
        log.info("parsing search page: {}", url);
        String content = OkHttpUtility.getContent(url, client);
        if (isContentInvalid(content)) {
            log.error("curse forge cookie is invalid. could not parse {}", url);
        } else {
            if (function.parse(messageModel, content).isEmpty()) {
                log.info("no more results");
            } else {
                String url_string = url.toString();
                try {
                    String page_string = StringUtility.getStringBetweenRegex(url_string, marker, AMPERSAND);
                    int page = Integer.parseInt(page_string);
                    int next = page + 1;
                    String newWebsiteURL = url_string.replace(marker + page, marker + next);
                    WebsiteModel wm = new WebsiteModel.Builder(newWebsiteURL).build();
                    MessageModel singleton = new MessageModel.Builder(messageModel.directory, wm).build();
                    send(singleton);
                } catch (Exception ex) {
                    log.error("unable to search url: {}", url_string);
                }
            }
        }
    }

}
