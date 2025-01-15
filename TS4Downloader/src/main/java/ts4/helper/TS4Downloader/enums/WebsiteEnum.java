package ts4.helper.TS4Downloader.enums;

import lombok.extern.slf4j.Slf4j;
import okhttp3.OkHttpClient;
import org.apache.logging.log4j.util.Strings;
import org.checkerframework.checker.units.qual.A;
import ts4.helper.TS4Downloader.utilities.OkHttpUtility;
import ts4.helper.TS4Downloader.utilities.StringUtility;
import ts4.helper.TS4Downloader.utilities.URLUtility;

import java.io.IOException;
import java.net.URL;
import java.util.*;
import java.util.stream.Collectors;

import static ts4.helper.TS4Downloader.enums.DomainEnum.PATREON;
import static ts4.helper.TS4Downloader.enums.DomainEnum.SIMS_FINDS;
import static ts4.helper.TS4Downloader.enums.DomainEnum.CURSE_FORGE;
import static ts4.helper.TS4Downloader.enums.DomainEnum.FORGE_CDN;

import static ts4.helper.TS4Downloader.constants.StringConstants.AMPERSAND;
import static ts4.helper.TS4Downloader.constants.StringConstants.COMMA;
import static ts4.helper.TS4Downloader.constants.StringConstants.EMPTY;
import static ts4.helper.TS4Downloader.constants.StringConstants.SINGLE_QUOTE;

@Slf4j
public enum WebsiteEnum {

    PATREON_POSTS("www.%s/posts/", PATREON),
    PATREON_FILE("www.%s/file?", PATREON),
    SIMS_FINDS_DOWNLOADS("www.%s/downloads/",SIMS_FINDS),
    SIMS_FINDS_CONTINUE("www.%s/continue?", SIMS_FINDS),
    SIMS_FINDS_DOWNLOAD("click.%s/download?", SIMS_FINDS),
    //SIMS_FINDS_DOWNLOAD_EXTERNAL("click.%s/download?flid=0", SIMS_FINDS),
    //SIMS_FINDS_DOWNLOAD_DIRECT("click.%s/download?flid=1", SIMS_FINDS),
    CURSE_FORGE_CAS("www.%s/sims4/create-a-sim/", CURSE_FORGE),
    CURSE_FORGE_MEMBERS("www.%s/members/", CURSE_FORGE),
    CURSE_FORGE_CREATORS("my.%s/?", CURSE_FORGE),
    CURSE_FORGE_API("www.%s/api/v1/mods/", CURSE_FORGE),
    CURSE_FORGE_CDN("edge.%s/files/", FORGE_CDN);


//    public static final List<WebsiteEnum> TRANSITION_WEBSITES = new ArrayList<>() {{
//        add(PATREON_POSTS);
//        add(SIMS_FINDS_DOWNLOADS);
//        add(SIMS_FINDS_CONTINUE);
//        add(CURSE_FORGE_CAS);
//        add(CURSE_FORGE_MEMBERS);
//        add(CURSE_FORGE_CREATORS);
//    }};

    public final String domain;
    public final DomainEnum domainEnum;

    WebsiteEnum(String format, DomainEnum domainEnum) {
        this.domain = String.format(format, domainEnum.name);
        this.domainEnum = domainEnum;
    }

    public static WebsiteEnum getByURL(URL url) {
        for (WebsiteEnum websiteEnum : values()) {
            if (url.toString().contains(websiteEnum.domain)) return websiteEnum;
        }
        return null;
    }

        /*
    [www.patreon.com/file?]                                 www.patreon.com/posts/

www.simsfinds.com/continue?key=                         www.simsfinds.com/downloads/
click.simsfinds.com/download?flid=                      www.simsfinds.com/continue?
*any*                                                   click.simsfinds.com/download?

    www.curseforge.com/api/v1/mods/                         www.curseforge.com/sims4/create-a-sim/
[www.curseforge.com/sims4/create-a-sim]                 www.curseforge.com/members/
[edge.forgecdn.net/files/]                              alenaivanisova.my.curseforge.com/?
     */

    private static List<String> LIST = new ArrayList<>();
    private static OkHttpClient OK_HTTP_CLIENT = new OkHttpClient();

    @FunctionalInterface
    private interface ParseFunction {
        List<String> parse(String content);
    }

    public List<URL> getURLs(URL url, OkHttpClient client) {
        LIST = new ArrayList<>();
        OK_HTTP_CLIENT = client;
        ParseFunction parse;
        try {
            switch (this) {
                case CURSE_FORGE_MEMBERS: {
                    parse = (content) ->
                            StringUtility.getSetBetweenRegex(content, "btn-cta\" href=\"/", "\">")
                                    .stream()
                                    .filter(str -> str.contains("/install"))
                                    .map(str -> str.replace("/install", ""))
                                    .map(str -> String.format("https://www.%s/%s", CURSE_FORGE.name, str))
                                    .toList();
                    return getURLs(url, "page=", parse);
                }
                case CURSE_FORGE_CREATORS: {
                    parse = (content) ->
                            StringUtility.getSetBetweenRegex(content, "\"downloadLink\":\"", SINGLE_QUOTE)
                                    .stream()
                                    .map(s -> s.replaceAll("\\\\", EMPTY))
                                    .toList();
                    return getURLs(url, "projectsPage=", parse);
                }
                case PATREON_FILE, CURSE_FORGE_CDN, CURSE_FORGE_API: {
                    return null;
                }
                case SIMS_FINDS_DOWNLOAD: {
                    String content = StringUtility.getStringBetweenRegex(url.toString(), "flid=", AMPERSAND);
                    long number = Long.parseLong(content);
                    if (number > 0) return null;
                }
                default: {
                    try {
                        String content = OkHttpUtility.getContent(url, client);
                        return getURLs(url, content);
                    } catch (Exception e) {
                        return getDefaultURLs(String.format("could not get content for url: %s", url));
                    }
                }
            }
        } catch (Exception e) {
            return getDefaultURLs(String.format("exception thrown for url: %s", url));
        }
    }

    private List<URL> getURLs(URL url, String content) {
        switch (this) {
            case PATREON_POSTS: {
                Set<String> set = StringUtility.getSetBetweenRegex(content, "<a href=\"/file?", SINGLE_QUOTE);
                List<String> list = set.stream()
                        .map(s -> "https://www.patreon.com/file?" + s.replace("amp;", EMPTY))
                        .toList();
                return getURLs(url, list);
            }
            case SIMS_FINDS_DOWNLOADS: {
                String key = StringUtility.getStringBetweenRegex(content, "key=", SINGLE_QUOTE);
                return getURL(url, "https://www.simsfinds.com/continue?key=" + key);
            }
            case SIMS_FINDS_CONTINUE: {
                String[] info = StringUtility.getStringBetweenRegex(content, "data-at5t768r9=\"", SINGLE_QUOTE)
                        .split(COMMA);
                String flid = StringUtility.getStringBetweenRegex(content, "data-at8r136r7=\"", SINGLE_QUOTE);
                String pass = StringUtility.getStringBetweenRegex(content, "data-passe=\"", SINGLE_QUOTE);

                Map<String, String> map = new HashMap<>();
                map.put("cid", info[0]);
                map.put("key", info[1]);
                map.put("version", info[3]);
                map.put("pass", pass);
                map.put("flid", flid);

                List<String> list = map.entrySet().stream()
                        .map(e -> String.format("%s=%s", e.getKey(), e.getValue()))
                        .collect(Collectors.toList());
                return getURL(url, "https://click.simsfinds.com/download?" + String.join(AMPERSAND, list));
            }
            case SIMS_FINDS_DOWNLOAD: {
                if (url.toString().contains("flid=0")) {
                    String external_url = StringUtility.getStringBetweenRegex(content, "<title>", "</title");
                    return getURL(url, external_url);
                } else {
                    return null;
                }
            }
            case CURSE_FORGE_CAS: {
                if (content.contains("Just a moment...")) {
                    return getDefaultURLs("curse forge cookie is invalid");
                } else {
                    String download_url = "https://www.curseforge.com/api/v1/mods/%s/files/%s/download";
                    String id1 = StringUtility.getStringBetweenRegex(content, "\"identifier\":\"", SINGLE_QUOTE);
                    String id2 = StringUtility.getStringBetweenRegex(content, "\"mainFile\":{\"id\":", COMMA);
                    String source_url = String.format(download_url, id1, id2);
                    return getURL(url, source_url);
                }
            }
            default: {
                return new ArrayList<>();
            }
        }
    }

    private List<URL> getURL(URL url, String string) {
        return getURLs(url, Collections.singletonList(string));
    }

    private List<URL> getURLs(URL url, List<String> list) {
        try {
            List<URL> urls = new ArrayList<>();
            for (String s: list) urls.add(URLUtility.createURL(s));
            return urls;
        } catch (Exception e) {
            String strings = String.join(", ", list);
            return getDefaultURLs(String.format("could not get urls for %s from list: [%s]", url.toString(), strings));
        }
    }

    private static List<URL> getDefaultURLs(String message) {
        log.error(message);
        return new ArrayList<>();
    }

    private static List<URL> getURLs(URL url, String page_marker, ParseFunction parseFunction) {
        try {
            String page_string = StringUtility.getStringBetweenRegex(url.toString(), page_marker, "&");
            int next, page = Integer.parseInt(page_string);
            log.info("searching page {}", page);
            String content = OkHttpUtility.getContent(url, OK_HTTP_CLIENT);
            List<String> links = new ArrayList<>(parseFunction.parse(content));
            if (links.isEmpty()) {
                if (content.contains("Just a moment...")) {
//                    log.info("retrying {}", url);
//                    Thread.sleep(2000);
//                    return getURLs(url, page_marker, parseFunction);
                    return getDefaultURLs(String.format("could not parse url: %s", url));
                } else {
                    List<URL> urls = new ArrayList<>();
                    for(String str : LIST) urls.add(URLUtility.createURL(str));
                    return urls;
                }
            } else {
                next = page + 1;
                for (String link : links) log.info(link);
                LIST.addAll(links);
                String urlString = url.toString().replace(page_marker + page, page_marker + next);
                URL newUrl = URLUtility.createURL(urlString);
                return getURLs(newUrl, page_marker, parseFunction);
            }
        } catch (Exception e) {
            return getDefaultURLs("error when parsing " + url);
        }
    }
//
//    public List<URL> getURLs(URL url, OkHttpClient client) {
//        LIST = new ArrayList<>();
//        OK_HTTP_CLIENT = client;
//        ParseFunction parse;
//        return switch (this) {
//            case CURSE_FORGE_MEMBERS: {
//                parse = (content) ->
//                        StringUtility.getSetBetweenRegex(content, "btn-cta\" href=\"/", "\">")
//                                .stream()
//                                .filter(str -> str.contains("/install"))
//                                .map(str -> str.replace("/install", ""))
//                                .map(str -> String.format("https://www.%s/%s", CURSE_FORGE.name, str))
//                                .toList();
//                yield getURLs(url, "page=", parse);
//            }
//            case CURSE_FORGE_CREATORS: {
//                parse = (content) ->
//                        StringUtility.getSetBetweenRegex(content, "\"downloadLink\":\"", SINGLE_QUOTE)
//                                .stream()
//                                .map(s -> s.replaceAll("\\\\", EMPTY))
//                                .toList();
//                yield getURLs(url, "projectsPage=", parse);
//            }
//            default: {
//                try {
//                    String content = OkHttpUtility.getContent(url, client);
//                    yield getURLs(url, content);
//                } catch (Exception e) {
//                    yield null;
//                }
//            }
////            default: { yield getDefaultURLs(String.format("unknown how to handle %s", url)); }
//        };
//    }
//
//    private List<URL> getURLs(URL url, String content) {
//        switch (this) {
//            case PATREON_POSTS: {
//                Set<String> set = StringUtility.getSetBetweenRegex(content, "<a href=\"/file?", SINGLE_QUOTE);
//                List<String> list = set.stream()
//                        .map(s -> "https://www.patreon.com/file?" + s.replace("amp;", EMPTY))
//                        .toList();
//                return getURLs(url, list);
//            }
//            case SIMS_FINDS_DOWNLOADS: {
//                String key = StringUtility.getStringBetweenRegex(content, "key=", SINGLE_QUOTE);
//                return getURL(url, "https://www.simsfinds.com/continue?key=" + key);
//            }
//            case SIMS_FINDS_CONTINUE: {
//                String[] info = StringUtility.getStringBetweenRegex(content, "data-at5t768r9=\"", SINGLE_QUOTE).split(COMMA);
//                String flid = StringUtility.getStringBetweenRegex(content, "data-at8r136r7=\"", SINGLE_QUOTE);
//                String pass = StringUtility.getStringBetweenRegex(content, "data-passe=\"", SINGLE_QUOTE);
//
//                Map<String, String> map = new HashMap<>();
//                map.put("cid", info[0]);
//                map.put("key", info[1]);
//                map.put("version", info[3]);
//                map.put("pass", pass);
//                map.put("flid", flid);
//
//                List<String> list = map.entrySet().stream()
//                        .map(e -> String.format("%s=%s", e.getKey(), e.getValue()))
//                        .collect(Collectors.toList());
//                return getURL(url, "https://click.simsfinds.com/download?" + String.join(AMPERSAND, list));
//            }
//            case SIMS_FINDS_DOWNLOAD: {
//                if (url.toString().contains("flid=0")) {
//                    String external_url = StringUtility.getStringBetweenRegex(content, "<title>", "</title");
//                    return getURL(url, external_url);
//                } else {
//                    return null;
//                }
//            }
//            case CURSE_FORGE_CAS: {
//                String download_url = "https://www.curseforge.com/api/v1/mods/%s/files/%s/download";
//                String id1 = StringUtility.getStringBetweenRegex(content, "\"identifier\":\"", SINGLE_QUOTE);
//                String id2 = StringUtility.getStringBetweenRegex(content, "\"mainFile\":{\"id\":", COMMA);
//                String source_url = String.format(download_url, id1, id2);
//                return getURL(url, source_url);
//            }
//            default: {
//                return null;
////                return getDefaultURLs(String.format("unknown how to handle %s content: %s", this.domain, content));
//            }
//        }
//    }
//
//    private List<URL> getURL(URL url, String string) {
//        return getURLs(url, Collections.singletonList(string));
//    }
//
//    private List<URL> getURLs(URL url, List<String> list) {
//        try {
//            List<URL> urls = new ArrayList<>();
//            for (String s: list) urls.add(URLUtility.createURL(s));
//            return urls;
//        } catch (Exception e) {
//            String strings = String.join(", ", list);
//            return getDefaultURLs(String.format("could not get urls for %s from list: [%s]", url.toString(), strings));
//        }
//    }
//
//    private static List<URL> getURLs(URL url, String page_marker, ParseFunction parseFunction) {
//        try {
//            String page_string = StringUtility.getStringBetweenRegex(url.toString(), page_marker, "&");
//            int next, page = Integer.parseInt(page_string);
//            log.info("searching page {}", page);
//            String content = OkHttpUtility.getContent(url, OK_HTTP_CLIENT);
//            List<String> links = new ArrayList<>(parseFunction.parse(content));
//            if (links.isEmpty()) {
//                if (content.contains("Just a moment...")) {
//                    log.info("retrying {}", url);
//                    Thread.sleep(2000);
//                    return getURLs(url, page_marker, parseFunction);
//                } else {
//                    List<URL> urls = new ArrayList<>();
//                    for(String str : LIST) urls.add(URLUtility.createURL(str));
//                    return urls;
//                }
//            } else {
//                next = page + 1;
//                for (String link : links) log.info(link);
//                LIST.addAll(links);
//                String urlString = url.toString().replace(page_marker + page, page_marker + next);
//                URL newUrl = URLUtility.createURL(urlString);
//                return getURLs(newUrl, page_marker, parseFunction);
//            }
//        } catch (Exception e) {
//            return getDefaultURLs("error when parsing " + url);
//        }
//    }
//
//    private static List<URL> getDefaultURLs(String message) {
//        log.error(message);
//        return new ArrayList<>();
//    }

}
