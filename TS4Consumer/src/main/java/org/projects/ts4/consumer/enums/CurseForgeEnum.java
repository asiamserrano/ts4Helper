package org.projects.ts4.consumer.enums;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.projects.ts4.avro.WebsiteModel;
import org.projects.ts4.consumer.producers.WebsiteProducer;
import org.projects.ts4.consumer.utlities.WebsiteUtility;
import org.projects.ts4.utility.constructors.WebsiteDomain;
import org.projects.ts4.utility.enums.ResponseEnum;
import org.projects.ts4.utility.utilities.OkHttpUtility;
import org.projects.ts4.utility.utilities.StringUtility;
import org.projects.ts4.utility.utilities.URLUtility;

import java.net.URL;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

import static org.projects.ts4.utility.constants.StringConstants.AMPERSAND;
import static org.projects.ts4.utility.constants.StringConstants.BACK_SLASHES;
import static org.projects.ts4.utility.constants.StringConstants.COMMA;
import static org.projects.ts4.utility.constants.StringConstants.EMPTY;
import static org.projects.ts4.utility.constants.StringConstants.FORWARD_SLASH;
import static org.projects.ts4.utility.constants.StringConstants.SINGLE_QUOTE;
import static org.projects.ts4.utility.constructors.SecondLevelDomain.CURSE_FORGE;
import static org.projects.ts4.utility.constructors.SecondLevelDomain.FORGE_CDN;
import static org.projects.ts4.utility.constructors.SubDomain.EDGE;
import static org.projects.ts4.utility.constructors.SubDomain.MY;
import static org.projects.ts4.utility.constructors.SubDomain.WWW;
import static org.projects.ts4.utility.constructors.TopLevelDomain.COM;
import static org.projects.ts4.utility.constructors.TopLevelDomain.NET;

@Slf4j
public class CurseForgeEnum extends BaseEnumImpl {

//    public static void main(String[] args) {
//        File directory = new File("/Users/asiaserrano/ChromeDownloads/download_2025-02-04_20.19.24");
//
////        WebsiteModel og = new WebsiteModel();
////        og.setUrl("https://www.curseforge.com/members/ssalon1/projects?page=2&pageSize=20&sortBy=ReleaseDate&sortOrder=Desc");
////        og.setDirectory(directory.getAbsolutePath());
//
//        WebsiteModel websiteModel = new WebsiteModel();
//        websiteModel.setUrl("https://www.curseforge.com/sims4/create-a-sim/ssalon-female-hairstyle-hb159");
//        websiteModel.setDirectory(directory.getAbsolutePath());
////        websiteModel.setPrevious(og);
//
//        CurseForgeEnum curseForgeEnum = (CurseForgeEnum) CurseForgeEnum.valueOf(websiteModel);
//        if (curseForgeEnum == null) {
//            System.out.println("null");
//        } else {
//            OkHttpClient okHttpClient = new OkHttpClient();
//            KafkaTemplate<String, WebsiteModel> template = new KafkaTemplate<>(new DefaultKafkaProducerFactory<>(
//                    new HashMap<>() {{
//                put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092");
//                put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
//                put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, KafkaAvroSerializer.class);
//                put(SCHEMA_REGISTRY_URL_HEADER, SCHEMA_REGISTRY_URL_VALUE);
//            }}));
//            KafkaTopics kafkaTopics = new KafkaTopics.Builder()
//                    .add(KafkaTopicEnum.DOWNLOADER, "ts4-downloader-topic")
//                    .add(KafkaTopicEnum.CONSUMER, "ts4-consumer-topic")
//                    .build();
//            WebsiteProducer websiteProducer = new WebsiteProducer(okHttpClient, template, kafkaTopics, directory);
//            curseForgeEnum.parse(websiteModel, websiteProducer);
//        }
//
//    }

    public static final CurseForgeEnum CURSE_FORGE_CAS = build(Enumeration.CF_CAS);
    public static final CurseForgeEnum CURSE_FORGE_API = build(Enumeration.CF_API);
    public static final CurseForgeEnum CURSE_FORGE_EDGE = build(Enumeration.CF_EDGE);
    public static final CurseForgeEnum CURSE_FORGE_MEMBERS = build(Enumeration.CF_MEMBERS);
    public static final CurseForgeEnum CURSE_FORGE_PROJECTS = build(Enumeration.CF_PROJECTS);

    public static final List<CurseForgeEnum> ALL = new ArrayList<>() {{
        add(CURSE_FORGE_CAS);
        add(CURSE_FORGE_API);
        add(CURSE_FORGE_EDGE);
        add(CURSE_FORGE_MEMBERS);
        add(CURSE_FORGE_PROJECTS);
    }};

    public final Enumeration enumeration;

    @AllArgsConstructor
    public enum Enumeration {
        CF_CAS("CURSE_FORGE_LINK", "/sims4/create-a-sim/"),
        CF_API("CURSE_FORGE_DOWNLOAD", "/api/v1/mods/"),
        CF_EDGE( "FORGE_CDN_DOWNLOAD", "/files/"),
        CF_MEMBERS( "CURSE_FORGE_MEMBERS", "/members/"),
        CF_PROJECTS("CURSE_FORGE_PROJECTS", "projectsPage");
        public final String value;
        public final String parameter;

        public WebsiteDomain getWebsiteDomain() {
            return switch (this) {
                case CF_EDGE -> new WebsiteDomain(EDGE, FORGE_CDN, NET);
                case CF_PROJECTS -> new WebsiteDomain(MY, CURSE_FORGE, COM);
                default -> new WebsiteDomain(WWW, CURSE_FORGE, COM);
            };

//            switch (this) {
//                case CF_EDGE -> {
//                    return new WebsiteDomain(EDGE, FORGE_CDN, NET);
//                }
//                case CF_PROJECTS -> {
//                    return new WebsiteDomain(MY, CURSE_FORGE, COM);
//                }
//                default -> { return new WebsiteDomain(WWW, CURSE_FORGE, COM); }
//            }
        }
    }

    private CurseForgeEnum(String value, WebsiteDomain domain, String parameter, Enumeration enumeration) {
        super(value, domain, parameter);
        this.enumeration = enumeration;
    }

    private static CurseForgeEnum build(Enumeration enumeration) {
        String value = enumeration.value;
        String parameter = enumeration.parameter;
        WebsiteDomain domain = enumeration.getWebsiteDomain();
        return new CurseForgeEnum(value, domain, parameter, enumeration);
    }

    @FunctionalInterface
    private interface ParseFunction {
        List<WebsiteModel> parse(WebsiteModel model, String content);
    }

    @Override
    public void parse(WebsiteModel websiteModel, WebsiteProducer websiteProducer) {
        try {
            switch (this.enumeration) {
                case CF_CAS: {
                    String content = OkHttpUtility.getContent(websiteModel, websiteProducer.okHttpClient);
                    if (isContentInvalid(content)) {
//                        log.error("curse forge cookie is invalid. cannot parse {}", websiteModel.getUrl());
                        WebsiteUtility.print(websiteModel, ResponseEnum.FAILURE, websiteProducer);
                    } else {
                        String projectId = StringUtility.getStringBetweenRegex(content, "\"project\":{\"id\":", COMMA);
                        String info = StringUtility.getStringBetweenRegex(content, "\"sourceRepoType\":", "\"displayName\"");
                        String id = StringUtility.getStringBetweenRegex(info, "[{id:", ",fileName");
                        String filename = StringUtility.getStringBetweenRegex(info, "fileName:", COMMA);
                        String urlString = String.format("https://www.curseforge.com/api/v1/mods/%s/files/%s/download", projectId, id);
                        WebsiteModel singleton = new WebsiteModel();
                        singleton.setUrl(urlString);
                        singleton.setFilename(filename);
                        singleton.setPrevious(websiteModel);
                        singleton.setDirectory(websiteModel.getDirectory());
                        CurseForgeEnum.CURSE_FORGE_API.parse(singleton, websiteProducer);
                    }
                    break;
                }
                case CF_PROJECTS: {
                    ParseFunction parse = (model, content) ->
                            StringUtility.getSetBetweenRegex(content, "\"downloadLink\":\"", SINGLE_QUOTE)
                                    .parallelStream()
                                    .map(str -> {
                                        String url = str.replaceAll(BACK_SLASHES, EMPTY);
                                        String[] parts = url.split(FORWARD_SLASH);
                                        String filename = URLDecoder.decode(parts[parts.length - 1], StandardCharsets.UTF_8);
                                        return parse(url, CurseForgeEnum.CURSE_FORGE_EDGE, filename, model, websiteProducer);
                                    })
                                    .toList();
                    parse(websiteModel, "projectsPage=", parse, websiteProducer);
                    break;
                }
                case CF_MEMBERS: {
                    ParseFunction parse = (model, content) ->
                            StringUtility.getSetBetweenRegex(content, "<a class=\" download-cta btn-cta\" href=\"/", "/download")
                                    .parallelStream()
                                    .map(websiteDomain::getHttpUrl)
                                    .map(httpurl -> parse(httpurl.toString(), CURSE_FORGE_CAS, EMPTY, model, websiteProducer))
                                    .toList();
                    parse(websiteModel, "page=", parse, websiteProducer);
                    break;
                }
                default: {
                    WebsiteUtility.print(websiteModel, ResponseEnum.DOWNLOAD, websiteProducer);
                    break;
                }
            }
        } catch (Exception e) {
            WebsiteUtility.print(websiteModel, ResponseEnum.ERROR, websiteProducer);
        }
    }

    private WebsiteModel parse(String url, CurseForgeEnum curseForgeEnum, String filename, WebsiteModel model, WebsiteProducer websiteProducer) {
        WebsiteModel singleton = new WebsiteModel();
        singleton.setUrl(url);
        singleton.setFilename(filename);
        singleton.setPrevious(model.getPrevious());
        singleton.setDirectory(model.getDirectory());
        curseForgeEnum.parse(singleton, websiteProducer);
        return singleton;
    }

    private void parse(WebsiteModel websiteModel, String marker, ParseFunction function, WebsiteProducer websiteProducer) {
        URL url = URLUtility.createURL(websiteModel);
        log.info("parsing search page: {}", url);
        String content = OkHttpUtility.getContent(url, websiteProducer.okHttpClient);
        if (isContentInvalid(content)) {
//            log.error("curse forge cookie is invalid. could not parse {}", url);
            WebsiteUtility.print(websiteModel, ResponseEnum.FAILURE, websiteProducer);
        } else {
            if (function.parse(websiteModel, content).isEmpty()) {
//                log.info("no more results");
                WebsiteUtility.print(websiteModel, ResponseEnum.COMPLETE, websiteProducer);
            } else {
                String url_string = url.toString();
                try {
                    String page_string = StringUtility.getStringBetweenRegex(url_string, marker, AMPERSAND);
                    int page = Integer.parseInt(page_string);
                    int next = page + 1;
                    String newWebsiteURL = url_string.replace(marker + page, marker + next);
                    WebsiteModel singleton = new WebsiteModel();
                    singleton.setUrl(newWebsiteURL);
                    singleton.setDirectory(websiteModel.getDirectory());
                    parse(singleton, websiteProducer);
                } catch (Exception ex) {
                    WebsiteUtility.print(websiteModel, ResponseEnum.ERROR, websiteProducer);
                }
            }
        }
    }

    private static boolean isContentInvalid(String content) {
        return content.contains("Just a moment...");
    }
}
