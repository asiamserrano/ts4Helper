package org.projects.ts4.consumer.enums;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import okhttp3.OkHttpClient;
import org.projects.ts4.avro.WebsiteModel;
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

import static org.projects.ts4.utility.constants.StringConstants.*;
import static org.projects.ts4.utility.constants.StringConstants.EMPTY;
import static org.projects.ts4.utility.constructors.SecondLevelDomain.CURSE_FORGE;
import static org.projects.ts4.utility.constructors.SecondLevelDomain.FORGE_CDN;
import static org.projects.ts4.utility.constructors.SubDomain.EDGE;
import static org.projects.ts4.utility.constructors.SubDomain.MY;
import static org.projects.ts4.utility.constructors.SubDomain.WWW;
import static org.projects.ts4.utility.constructors.TopLevelDomain.COM;
import static org.projects.ts4.utility.constructors.TopLevelDomain.NET;

@Slf4j
public class CurseForgeEnum extends BaseEnumImpl {

    private static final OkHttpClient CURSE_FORGE_CLIENT = new OkHttpClient();

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
            switch (this) {
                case CF_EDGE -> {
                    return new WebsiteDomain(EDGE, FORGE_CDN, NET);
                }
                case CF_PROJECTS -> {
                    return new WebsiteDomain(MY, CURSE_FORGE, COM);
                }
                default -> { return new WebsiteDomain(WWW, CURSE_FORGE, COM); }
            }
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

    public static CurseForgeEnum valueOf(WebsiteModel websiteModel) {
        BaseEnumImpl baseEnumImpl = BaseEnumImpl.valueOf(websiteModel);
        return baseEnumImpl == null ? null : baseEnumImpl instanceof CurseForgeEnum ? (CurseForgeEnum) baseEnumImpl : null;
    }

    @FunctionalInterface
    private interface ParseFunction {
        List<WebsiteModel> parse(WebsiteModel model, String content);
    }

    @Override
    public void parse(WebsiteModel websiteModel) {
        try {
            switch (this.enumeration) {
                case CF_CAS: {
                    String content = OkHttpUtility.getContent(websiteModel, CURSE_FORGE_CLIENT);
                    if (isContentInvalid(content)) {
//                        log.error("curse forge cookie is invalid. cannot parse {}", websiteModel.getUrl());
                        BaseEnum.print(websiteModel, ResponseEnum.FAILURE);
                    } else {
                        String projectId = StringUtility.getStringBetweenRegex(content, "Project ID</dt><dd>", "</dd>");
                        String info = StringUtility.getStringBetweenRegex(content, "\"mainFile\":{\"id\":", ",\"displayName\"");
                        String id = info.split(",")[0];
                        String filename = info.split("fileName:")[1];
                        String urlString = String.format("https://www.curseforge.com/api/v1/mods/%s/files/%s/download", projectId, id);
                        WebsiteModel singleton = new WebsiteModel();
                        singleton.setUrl(urlString);
                        singleton.setFilename(filename);
                        singleton.setPrevious(websiteModel);
                        CurseForgeEnum.CURSE_FORGE_API.parse(singleton);
//                    parse(singleton, CurseForgeEnum.CURSE_FORGE_API);
                    }
                    break;
                }
                case CF_PROJECTS: {
                    ParseFunction parse = (model, content) ->
                            StringUtility.getSetBetweenRegex(content, "\"downloadLink\":\"", SINGLE_QUOTE)
                                    .parallelStream()
                                    .map(str -> {
                                        String url = str.replaceAll(BACK_SLASHES, EMPTY);
                                        String[] parts = url.split("/");
                                        String filename = URLDecoder.decode(parts[parts.length - 1], StandardCharsets.UTF_8);
                                        return parse(url, CurseForgeEnum.CURSE_FORGE_EDGE, filename, model);
                                    })
                                    .toList();
                    parse(websiteModel, "projectsPage=", parse);
                    break;
                }
                case CF_MEMBERS: {
                    ParseFunction parse = (model, content) -> {
//                    WebsiteDomain websiteDomain = curseForgeEnum.websiteDomain;
                        return StringUtility.getSetBetweenRegex(content, "<a class=\" download-cta btn-cta\" href=\"/", "/download")
                                .parallelStream()
                                .map(websiteDomain::getHttpUrl)
                                .map(httpurl -> parse(httpurl.toString(), CURSE_FORGE_CAS, EMPTY, model))
                                .toList();
                    };
                    parse(websiteModel, "page=", parse);
                    break;
                }
                default: {
                    BaseEnum.print(websiteModel, ResponseEnum.DOWNLOAD);
                    break;
                }
            }
        } catch (Exception e) {
            BaseEnum.print(websiteModel, ResponseEnum.ERROR);
        }
    }

    private WebsiteModel parse(String url, CurseForgeEnum curseForgeEnum, String filename, WebsiteModel model) {
        WebsiteModel singleton = new WebsiteModel();
        singleton.setUrl(url);
        singleton.setFilename(filename);
        singleton.setPrevious(model.getPrevious());
        curseForgeEnum.parse(singleton);
//        parse(singleton, curseForgeEnum);
        return singleton;
    }

    private void parse(WebsiteModel websiteModel, String marker, ParseFunction function) {
        URL url = URLUtility.createURL(websiteModel);
        log.info("parsing search page: {}", url);
        String content = OkHttpUtility.getContent(url, CURSE_FORGE_CLIENT);
        if (isContentInvalid(content)) {
//            log.error("curse forge cookie is invalid. could not parse {}", url);
            BaseEnum.print(websiteModel, ResponseEnum.FAILURE);
        } else {
            if (function.parse(websiteModel, content).isEmpty()) {
//                log.info("no more results");
                BaseEnum.print(websiteModel, ResponseEnum.COMPLETE);
            } else {
                String url_string = url.toString();
                try {
                    String page_string = StringUtility.getStringBetweenRegex(url_string, marker, AMPERSAND);
                    int page = Integer.parseInt(page_string);
                    int next = page + 1;
                    String newWebsiteURL = url_string.replace(marker + page, marker + next);
                    WebsiteModel singleton = new WebsiteModel();
                    singleton.setUrl(newWebsiteURL);
                    parse(singleton);
//                    parse(singleton, this);
                } catch (Exception ex) {
//                    log.error("unable to search url: {}", url_string);
                    BaseEnum.print(websiteModel, ResponseEnum.ERROR);
                }
            }
        }
    }

    private static boolean isContentInvalid(String content) {
        return content.contains("Just a moment...");
    }
}
