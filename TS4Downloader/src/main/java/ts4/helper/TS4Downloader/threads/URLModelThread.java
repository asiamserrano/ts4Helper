package ts4.helper.TS4Downloader.threads;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import okhttp3.OkHttpClient;
import okhttp3.Response;
import ts4.helper.TS4Downloader.constructors.DomainPath;
import ts4.helper.TS4Downloader.enums.ExtensionEnum;
import ts4.helper.TS4Downloader.enums.WebsiteEnum;
import ts4.helper.TS4Downloader.models.DownloadResponse;
import ts4.helper.TS4Downloader.models.URLModel;
import ts4.helper.TS4Downloader.utilities.OkHttpUtility;
import ts4.helper.TS4Downloader.utilities.StringUtility;
import ts4.helper.TS4Downloader.utilities.URLUtility;
import ts4.helper.TS4Downloader.utilities.UnzipUtility;

import java.io.File;
import java.io.FileOutputStream;
import java.net.URL;
import java.net.URLDecoder;
import java.nio.channels.Channels;
import java.nio.channels.FileChannel;
import java.nio.channels.ReadableByteChannel;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;
import java.util.HashMap;
import java.util.ArrayList;
import java.util.Collections;
import java.util.concurrent.Callable;
import java.util.stream.Collectors;

import ts4.helper.TS4Downloader.enums.ResponseEnum;

import static ts4.helper.TS4Downloader.constants.StringConstants.COMMA;
import static ts4.helper.TS4Downloader.constants.StringConstants.SINGLE_QUOTE;
import static ts4.helper.TS4Downloader.constants.StringConstants.AMPERSAND;
import static ts4.helper.TS4Downloader.constants.StringConstants.EMPTY;
import static ts4.helper.TS4Downloader.constants.StringConstants.BACK_SLASHES;

@Slf4j
@AllArgsConstructor
public class URLModelThread implements Callable<DownloadResponse> {

    public final URLModel urlModel;
    public final OkHttpClient client;
    public final File directory;

    @Override
    public DownloadResponse call() throws Exception {
        URL url = urlModel.url;
        log.info("downloading link {}", url);
        WebsiteEnum websiteEnum = WebsiteEnum.getByURL(url);
        return getURLs(websiteEnum);
    }

    private DownloadResponse getURLs(WebsiteEnum websiteEnum) {
        if (websiteEnum == null) {
            return new DownloadResponse(ResponseEnum.UNKNOWN, urlModel);
        } else {
            switch (websiteEnum) {
                case PATREON_POSTS: {
                    String content = OkHttpUtility.getContent(urlModel, client);
                    String folder = StringUtility.getStringBetweenRegex(content, "<title>", "</title>");
                    URLModel newURLModel = new URLModel(urlModel.url, folder);
                    List<URLModel> list = StringUtility.getSetBetweenRegex(content, "{\"attributes\":{\"name\":\"", "\"},")
                            .parallelStream().map(s -> {
                                String filename = s.split(",")[0];
                                String url_string = s.split("url:")[1].replace("\\u0026i", "&i");
                                URL newURL = URLUtility.createURL(url_string);
                                return new URLModel(newURL, filename, newURLModel);
                            }).toList();
                    return new DownloadResponse(urlModel, list);
                }
                case SIMS_FINDS_DOWNLOADS: {
                    String content = OkHttpUtility.getContent(urlModel, client);
                    String name = StringUtility.getStringBetweenRegex(content, "<title id=\"title\">", "</title>");
                    URLModel newURLModel = new URLModel(urlModel.url, name);
                    String continue_string = StringUtility.getStringBetweenRegex(content, "data-continue=\"", "\"");
                    URL continueURl = URLUtility.createURL(continue_string);
                    URLModel singleton = new URLModel(continueURl, name, newURLModel);
                    return new DownloadResponse(ResponseEnum.SUCCESSFUL, newURLModel, singleton);
                }
                case SIMS_FINDS_CONTINUE: {
                    String content = OkHttpUtility.getContent(urlModel, client);
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
                    URL downloadURL = URLUtility.createURL(downloadURLString);
                    URLModel singleton = new URLModel(downloadURL, urlModel.name, urlModel);
                    return new DownloadResponse(ResponseEnum.SUCCESSFUL, urlModel, singleton);
                }
                case SIMS_FINDS_DOWNLOAD: {
                    URL url = urlModel.url;
                    if (url.toString().contains("flid=0")) {
                        String content = OkHttpUtility.getContent(url, client);
                        URL externalURL = URLUtility.createURL(StringUtility.getStringBetweenRegex(content, "<title>", "</title"));
                        URLModel singleton = new URLModel(externalURL, EMPTY, urlModel);
                        return new DownloadResponse(ResponseEnum.SUCCESSFUL, urlModel, singleton);
                    } else {
                        Response response = OkHttpUtility.sendRequest(url, client);
                        String filename = urlModel.name + ExtensionEnum.getExtension(response);
                        URLModel newURLModel = new URLModel(url, filename, urlModel.previous);
                        return download(newURLModel);
                    }
                }
                case CURSE_FORGE_CAS: {
                    //https://www.curseforge.com/sims4/create-a-sim/ssalon-hair-g126                  YES
                    //https://www.curseforge.com/sims4/create-a-sim/ssalon-hair-g126/download/6089380 NO
                    //https://www.curseforge.com/api/v1/mods/1061761/files/6089380/download
                    String content = OkHttpUtility.getContent(urlModel, client);
                    if (isContentInvalid(content)) {
                        return new DownloadResponse(ResponseEnum.FAILURE, urlModel);
                    } else {
                        String projectId = StringUtility.getStringBetweenRegex(content, "Project ID</dt><dd>", "</dd>");
                        String info = StringUtility.getStringBetweenRegex(content, "\"mainFile\":{\"id\":", ",\"displayName\"");
                        String id = info.split(",")[0];
                        String filename = info.split("fileName:")[1];
                        String urlString = String.format("https://www.curseforge.com/api/v1/mods/%s/files/%s/download", projectId, id);
                        URL url = URLUtility.createURL(urlString);
                        URLModel singleton = new URLModel(url, filename, urlModel);
                        return new DownloadResponse(ResponseEnum.SUCCESSFUL, urlModel, singleton);
                    }
                }
                case CURSE_FORGE_CREATORS: {
                    ParseFunction parse = (model, content) -> StringUtility
                            .getSetBetweenRegex(content, "\"downloadLink\":\"", SINGLE_QUOTE).parallelStream()
                            .map(str -> {
                                String string = str.replaceAll(BACK_SLASHES, EMPTY);
                                String[] parts = string.split("/");
                                String name = URLDecoder.decode(parts[parts.length - 1], StandardCharsets.UTF_8);
                                URL url = URLUtility.createURL(string);
                                return new URLModel(url, name, model);})
                            .toList();
                    return getResponse(urlModel, "projectsPage=", parse);
                }
                case CURSE_FORGE_MEMBERS: {
                    String url_head = WebsiteEnum.CURSE_FORGE_CAS.getHttpUrl().toString();
                    ParseFunction parse = (model, content) -> StringUtility
                            .getSetBetweenRegex(content, "<a class=\" download-cta btn-cta\" href=\"/", "/download")
                            .parallelStream().map(str -> URLUtility.createURL(url_head + str.replace(DomainPath.S4_CAS.value, EMPTY)))
                            .map(url -> new URLModel(url, EMPTY, model))
                            .toList();
                    return getResponse(urlModel, "page=", parse);
                }
                case PATREON_FILE, CURSE_FORGE_API, CURSE_FORGE_CDN: {
                    return download(urlModel);
                }
                default: {
                    return new DownloadResponse(ResponseEnum.UNKNOWN, urlModel);
                }
            }
        }
    }

    @FunctionalInterface
    private interface ParseFunction {
        List<URLModel> parse(URLModel urlModel, String content);
    }

    private DownloadResponse download(URLModel model) {
        File destination = new File(directory, model.name);
        URL source = model.url;
        try(FileOutputStream fileOutputStream = new FileOutputStream(destination)) {
            ReadableByteChannel readableByteChannel = Channels.newChannel(source.openStream());
            FileChannel fileChannel = fileOutputStream.getChannel();
            fileChannel.transferFrom(readableByteChannel, 0, Long.MAX_VALUE);
            log.info("downloaded url {} to {}", source, destination);
            if (ExtensionEnum.isZipExtension(destination)) UnzipUtility.unzipFile(destination);
            return new DownloadResponse(ResponseEnum.COMPLETE, model);
        } catch (Exception e) {
            log.error("unable to download url {} to {}", source, destination, e);
            return new DownloadResponse(ResponseEnum.FAILURE, model);
        }
    }

    private boolean isContentInvalid(String content) {
        return content.contains("Just a moment...");
    }

    private DownloadResponse getResponse(URLModel urlModel, String marker, ParseFunction function) {
        URL url = urlModel.url;
        log.info("parsing search page: {}", url);
        String content = OkHttpUtility.getContent(url, client);
        if (isContentInvalid(content)) {
            log.error("curse forge cookie is invalid. could not parse {}", urlModel.url);
            return new DownloadResponse(ResponseEnum.FAILURE, urlModel);
        } else {
            List<URLModel> models = new ArrayList<>(function.parse(urlModel, content));
            if (models.isEmpty()) {
                log.info("no more results");
                return new DownloadResponse(ResponseEnum.SUCCESSFUL, urlModel);
            } else {
                String url_string = url.toString();
                String page_string = StringUtility.getStringBetweenRegex(url_string, marker, AMPERSAND);
                int page = Integer.parseInt(page_string);
                int next = page + 1;
                String urlString = url_string.replace(marker + page, marker + next);
                URLModel newURLModel = new URLModel(URLUtility.createURL(urlString), EMPTY);
                List<URLModel> urls = new ArrayList<>(Collections.singleton(newURLModel));
                urls.addAll(models);
                return new DownloadResponse(ResponseEnum.SUCCESSFUL, urlModel, urls);
            }
        }
    }

}