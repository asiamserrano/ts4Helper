package ts4.helper.TS4Downloader.enums;

import lombok.extern.slf4j.Slf4j;
import okhttp3.OkHttpClient;
import ts4.helper.TS4Downloader.models.DownloadResponse;
import ts4.helper.TS4Downloader.utilities.OkHttpUtility;
import ts4.helper.TS4Downloader.utilities.StringUtility;
import ts4.helper.TS4Downloader.utilities.URLUtility;

import java.net.URL;
import java.util.*;
import java.util.stream.Collectors;

import static ts4.helper.TS4Downloader.constants.StringConstants.*;

import static ts4.helper.TS4Downloader.enums.DomainEnum.PATREON;
import static ts4.helper.TS4Downloader.enums.DomainEnum.SIMS_FINDS;
import static ts4.helper.TS4Downloader.enums.DomainEnum.CURSE_FORGE;
import static ts4.helper.TS4Downloader.enums.DomainEnum.FORGE_CDN;

import static ts4.helper.TS4Downloader.enums.ResponseEnum.SUCCESSFUL;
import static ts4.helper.TS4Downloader.enums.ResponseEnum.FAILURE;
import static ts4.helper.TS4Downloader.enums.ResponseEnum.UNKNOWN;
import static ts4.helper.TS4Downloader.enums.ResponseEnum.DOWNLOAD;

@Slf4j
public enum WebsiteEnum {
    // INPUT
    PATREON_POSTS("www.%s/posts/", PATREON),
    CURSE_FORGE_MEMBERS("www.%s/members/", CURSE_FORGE),
    CURSE_FORGE_CREATORS("my.%s/?", CURSE_FORGE),
    SIMS_FINDS_DOWNLOADS("www.%s/downloads/", SIMS_FINDS),
    CURSE_FORGE_CAS("www.%s/sims4/create-a-sim/", CURSE_FORGE),

    // TRANSITION
    SIMS_FINDS_CONTINUE("www.%s/continue?", SIMS_FINDS),

    // DOWNLOAD
    PATREON_FILE("www.%s/file?", PATREON),
    SIMS_FINDS_DOWNLOAD("click.%s/download?", SIMS_FINDS), // *SPECIAL*
    CURSE_FORGE_API("www.%s/api/v1/mods/", CURSE_FORGE),
    CURSE_FORGE_CDN("edge.%s/files/", FORGE_CDN);

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

    private static OkHttpClient OK_HTTP_CLIENT;

    @FunctionalInterface
    private interface ParseFunction {
        List<String> parse(String content);
    }

    public DownloadResponse getURLs(URL url, OkHttpClient client) {
        OK_HTTP_CLIENT = client;
        ParseFunction parse;
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
                                .map(s -> s.replaceAll(BACK_SLASHES, EMPTY))
                                .toList();
                return getURLs(url, "projectsPage=", parse);
            }
            case PATREON_FILE, CURSE_FORGE_CDN, CURSE_FORGE_API: {
                return new DownloadResponse(DOWNLOAD);
            }
            case SIMS_FINDS_DOWNLOAD: {
                long number = getFLIDLong(url);
                if (number > 0) return new DownloadResponse(DOWNLOAD);
            }
            default: {
                String content = OkHttpUtility.getContent(url, client);
                return getURLs(url, content);
            }
        }
    }

    private DownloadResponse getURLs(URL url, String content) {
        switch (this) {
            case PATREON_POSTS: {
                Set<String> set = StringUtility.getSetBetweenRegex(content, "<a href=\"/file?", SINGLE_QUOTE);
                List<String> list = set.stream()
                        .map(s -> "https://www.patreon.com/file?" + s.replace("amp;", EMPTY))
                        .toList();
                return getURLs(list);
            }
            case SIMS_FINDS_DOWNLOADS: {
                String key = StringUtility.getStringBetweenRegex(content, "key=", SINGLE_QUOTE);
                return getURL("https://www.simsfinds.com/continue?key=" + key);
            }
            case SIMS_FINDS_CONTINUE: {
                String SQ = SINGLE_QUOTE;
                String[] info = StringUtility.getStringBetweenRegex(content, "data-at5t768r9=\"", SQ).split(COMMA);
                String flid = StringUtility.getStringBetweenRegex(content, "data-at8r136r7=\"", SQ);
                String pass = StringUtility.getStringBetweenRegex(content, "data-passe=\"", SQ);
                Map<String, String> map = new HashMap<>() {{
                    put("cid", info[0]);
                    put("key", info[1]);
                    put("version", info[3]);
                    put("pass", pass);
                    put("flid", flid);
                }};

                List<String> list = map.entrySet().stream()
                        .map(e -> String.format("%s=%s", e.getKey(), e.getValue()))
                        .collect(Collectors.toList());
                return getURL("https://click.simsfinds.com/download?" + String.join(AMPERSAND, list));
            }
            case SIMS_FINDS_DOWNLOAD: {
                String external_url = StringUtility.getStringBetweenRegex(content, "<title>", "</title");
                return external_url.isEmpty() ? new DownloadResponse(UNKNOWN) : getURL(external_url);
            }
            case CURSE_FORGE_CAS: {
                if (isContentInvalid(content)) {
                    return getInvalidCookieURLs(url);
                } else {
                    String download_url = "https://www.curseforge.com/api/v1/mods/%s/files/%s/download";
                    String id1 = StringUtility.getStringBetweenRegex(content, "\"identifier\":\"", SINGLE_QUOTE);
                    String id2 = StringUtility.getStringBetweenRegex(content, "\"mainFile\":{\"id\":", COMMA);
                    String source_url = String.format(download_url, id1, id2);
                    return getURL(source_url);
                }
            }
            default: {
                return new DownloadResponse(UNKNOWN);
            }
        }
    }

    private long getFLIDLong(URL url) {
        String content = StringUtility.getStringBetweenRegex(url.toString(), "flid=", AMPERSAND);
        return Long.parseLong(content);
    }

    private DownloadResponse getURL(String string) {
        return getURLs(Collections.singletonList(string));
    }

    private DownloadResponse getURLs(List<String> list) {
        List<URL> urls = list.stream().map(URLUtility::createURL).toList();
        return new DownloadResponse(SUCCESSFUL, urls);
    }

    private DownloadResponse getDefaultURLs(String message, ResponseEnum responseEnum) {
        log.info(message);
        return new DownloadResponse(responseEnum);
    }

    private DownloadResponse getURLs(URL url, String page_marker, ParseFunction parseFunction) {
        log.info("parsing search page: {}", url);

        String content = OkHttpUtility.getContent(url, OK_HTTP_CLIENT);
        if (isContentInvalid(content)) {
            return getInvalidCookieURLs(url);
        } else {
            List<String> links = new ArrayList<>(parseFunction.parse(content));
            if (links.isEmpty()) {
                return getDefaultURLs("no more results", SUCCESSFUL);
            } else {
                String url_string = url.toString();
                String page_string = StringUtility.getStringBetweenRegex(url_string, page_marker, AMPERSAND);
                int page = Integer.parseInt(page_string);
                int next = page + 1;
                String urlString = url_string.replace(page_marker + page, page_marker + next);
                URL newUrl = URLUtility.createURL(urlString);
                List<URL> urls = new ArrayList<>(Collections.singleton(newUrl));
                for(String str : links) urls.add(URLUtility.createURL(str));
                return new DownloadResponse(SUCCESSFUL, urls);
            }
        }
    }

    private boolean isContentInvalid(String content) {
        return content.contains("Just a moment...");
    }

    private DownloadResponse getInvalidCookieURLs(URL url) {
        String message = String.format("curse forge cookie is invalid. could not parse %s", url);
        return getDefaultURLs(message, FAILURE);
    }

}
