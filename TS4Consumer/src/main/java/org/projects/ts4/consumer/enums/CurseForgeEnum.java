package org.projects.ts4.consumer.enums;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.projects.ts4.avro.WebsiteModel;
import org.projects.ts4.consumer.classes.WebsiteLogger;
import org.projects.ts4.utility.classes.Website;
import org.projects.ts4.utility.constructors.WebsiteDomain;
import org.projects.ts4.utility.enums.ResponseEnum;
import org.projects.ts4.utility.utilities.FileUtility;
import org.projects.ts4.utility.utilities.StringUtility;

import java.io.File;
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
    public void parse(WebsiteLogger websiteLogger) {
        WebsiteModel websiteModel = websiteLogger.websiteModel;
        try {
            switch (this.enumeration) {
                case CF_CAS: {
                    String content = websiteLogger.getContent();
                    if (isContentInvalid(content, websiteModel)) {
                        websiteLogger.print(ResponseEnum.INVALID);
                    } else {
                        String projectId = StringUtility.getStringBetweenRegex(content, "\"project\":{\"id\":", COMMA);
                        String info = StringUtility.getStringBetweenRegex(content, "\"sourceRepoType\":", "\"displayName\"");
                        String id = StringUtility.getStringBetweenRegex(info, "[{id:", ",fileName");
                        String name = StringUtility.getStringBetweenRegex(info, "fileName:", COMMA);
                        String url = String.format("https://www.curseforge.com/api/v1/mods/%s/files/%s/download", projectId, id);
                        WebsiteModel singleton = Website.build(url, name, websiteModel);
                        CurseForgeEnum.CURSE_FORGE_API.parse(websiteLogger.create(singleton));
                    }
                    break;
                }
                case CF_PROJECTS: {
                    ParseFunction parse = (model, content) ->
                            StringUtility.getSetBetweenRegex(content, "\"downloadLink\":\"", SINGLE_QUOTE)
                                    .parallelStream()
                                    .map(str -> {
                                        WebsiteLogger logger = websiteLogger.create(model);
                                        String url = str.replaceAll(BACK_SLASHES, EMPTY);
                                        String name = StringUtility.last(url, FORWARD_SLASH);
                                        String filename = URLDecoder.decode(name, StandardCharsets.UTF_8);
                                        return parse(logger, url, CURSE_FORGE_EDGE, filename);
                                    })
                                    .toList();
                    parse(websiteLogger, "projectsPage=", parse);
                    break;
                }
                case CF_MEMBERS: {
                    ParseFunction parse = (model, content) ->
                            StringUtility.getSetBetweenRegex(content, "<a class=\" download-cta btn-cta\" href=\"/", "/download")
                                    .parallelStream()
                                    .map(websiteDomain::getHttpUrl)
                                    .map(httpurl -> {
                                        String url = httpurl.toString();
                                        WebsiteLogger logger = websiteLogger.create(model);
                                        return parse(logger, url, CURSE_FORGE_CAS, EMPTY);
                                    })
                                    .toList();
                    parse(websiteLogger, "page=", parse);
                    break;
                }
                default: {
                    websiteLogger.print(ResponseEnum.DOWNLOAD);
                    break;
                }
            }
        } catch (Exception e) {
            websiteLogger.exception(e);
        }
    }

    private void parse(WebsiteLogger websiteLogger, String marker, ParseFunction function) {
        WebsiteModel websiteModel = websiteLogger.websiteModel;
        String url = websiteModel.getUrl();
        log.info("parsing search page: {}", url);
        String content = websiteLogger.getContent();
        if (isContentInvalid(content, websiteModel)) {
            websiteLogger.print(ResponseEnum.INVALID);
        } else {
            if (function.parse(websiteModel, content).isEmpty()) {
                websiteLogger.print(ResponseEnum.COMPLETE);
            } else {
                try {
                    File dir = FileUtility.createDirectory(websiteModel.getDirectory());
                    String page_string = StringUtility.getStringBetweenRegex(url, marker, AMPERSAND);
                    int page = Integer.parseInt(page_string);
                    int next = page + 1;
                    url = url.replace(marker + page, marker + next);
                    WebsiteModel singleton = Website.build(url, dir);
                    parse(websiteLogger.create(singleton));
                } catch (Exception e) {
                    websiteLogger.exception(e);
                }
            }
        }
    }

    private WebsiteModel parse(WebsiteLogger websiteLogger, String url, CurseForgeEnum curseForgeEnum, String name) {
        WebsiteModel model = websiteLogger.websiteModel;
        WebsiteModel singleton = Website.build(url, name, model, false);
        curseForgeEnum.parse(websiteLogger.create(singleton));
        return singleton;
    }

    private static boolean isContentInvalid(String content, WebsiteModel websiteModel) {
        boolean result = content.contains("Just a moment...");
        if (result) log.error("curse forge cookie is invalid. could not parse {}", websiteModel.getUrl());
        return result;
    }

}
