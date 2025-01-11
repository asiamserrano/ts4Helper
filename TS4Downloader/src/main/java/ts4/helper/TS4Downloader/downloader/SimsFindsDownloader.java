package ts4.helper.TS4Downloader.downloader;

import com.google.common.io.Resources;
import okhttp3.OkHttpClient;
import okhttp3.Response;
import okhttp3.Request;
import okhttp3.Call;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ts4.helper.TS4Downloader.constants.ExtensionEnum;
import ts4.helper.TS4Downloader.constants.SimsFindsEnum;
import ts4.helper.TS4Downloader.constants.WebsiteEnum;
import ts4.helper.TS4Downloader.model.SimsFindsModel;
import ts4.helper.TS4Downloader.utilities.FileUtility;
import ts4.helper.TS4Downloader.utilities.StringUtility;
import ts4.helper.TS4Downloader.utilities.URLUtility;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static ts4.helper.TS4Downloader.constants.StringConstants.SINGLE_QUOTE;
import static ts4.helper.TS4Downloader.constants.StringConstants.COMMA;
import static ts4.helper.TS4Downloader.constants.StringConstants.AMPERSAND;

import static ts4.helper.TS4Downloader.constants.SimsFindsEnum.DOWNLOADS;
import static ts4.helper.TS4Downloader.constants.SimsFindsEnum.CONTINUE;
import static ts4.helper.TS4Downloader.constants.SimsFindsEnum.DOWNLOAD;

public class SimsFindsDownloader {

    // External Link
    // https://www.simsfinds.com/downloads/319350/snowflake-boots-sims4
    // https://www.simsfinds.com/continue?key=f987fc317309e21ce0fdadaaa140c47e
    // https://click.simsfinds.com/download?flid=0&pass=2307102333&version=1675093910&key=f987fc317309e21ce0fdadaaa140c47e&cid=319350

    // Direct Link
    // https://www.simsfinds.com/downloads/316599/la-medusa-plataform-sandals-sims4
    // https://www.simsfinds.com/continue?key=9e4a77dd0c64425f1da52a38303d83e3
    // https://click.simsfinds.com/download?flid=173321671320709&pass=2307102333&version=1733216713&key=9e4a77dd0c64425f1da52a38303d83e3&cid=316599

    private static final Logger log = LoggerFactory.getLogger(SimsFindsDownloader.class);

    private static final Path SIMS_FINDS_PATH = Paths.get(WebsiteEnum.SIMS_FINDS.getUrl(),
            SimsFindsEnum.DOWNLOADS.getString());

    private static final OkHttpClient okHttpClient = new OkHttpClient();

    public static void main(String[] args) throws Exception {
        URL url = Resources.getResource("bookmarks_1_8_25.html");
//        URL url = Resources.getResource("input.txt");
        URLConnection connection = url.openConnection();
        InputStream inputStream = connection.getInputStream();
        InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
        BufferedReader reader = new BufferedReader(inputStreamReader);
        Stream<String> stream = reader.lines();
//        for (String string: stream.toArray(String[]::new)) {
//            URL source = URLUtility.createURL(string);
//            Response response = getResponse(source, okHttpClient);
//            String result = response.header("Content-Type");
//            System.out.println(result);
//        }


        stream.forEach(SimsFindsDownloader::parse);
//        String location = "/Users/asiaserrano/ChromeDownloads";
//        String file_name = "la-medusa-plataform-sandals";
//        File directory = new File(location, file_name);
//        URL source = URLUtility.createURL("https://click.simsfinds.com/download?flid=173321671320709&pass=2307102333&version=1733216713&key=9e4a77dd0c64425f1da52a38303d83e3&cid=316599");
//        download(source, directory);
    }

    public static boolean download(URL source, File directory, OkHttpClient okHttpClient) throws Exception {
        Response response = getResponse(source, okHttpClient);
        ExtensionEnum extensionEnum = ExtensionEnum.get(response);
        String file_name = directory.getName();
        File destination = new File(directory, file_name + extensionEnum.getExtension());
        return FileUtility.createDirectory(directory) && URLUtility.download(source, destination);
    }

    private static void parse(String string) {
        if (string.contains(SIMS_FINDS_PATH.toString())) {
            String url = string.split(SINGLE_QUOTE)[1];
            try {
                SimsFindsModel model = new SimsFindsModel(DOWNLOADS, url);
                download(model);
            } catch (Exception e) {
                log.error("could not parse string: {}", string);
            }
        }
    }

    private static void download(SimsFindsModel downloads_model) throws Exception {
        String downloads_content = getContent(downloads_model);
        SimsFindsModel continue_model = getContinueURL(downloads_content, downloads_model);
        String continue_content = getContent(continue_model);
        SimsFindsModel download_model = getDownloadURL(continue_content, continue_model);
        if (download_model.urlContains("flid=0")) {
            String download_content = getContent(download_model);
            String external_url = StringUtility.regexBetween(download_content, "<title>", "</title");
            System.out.println(external_url);
        } else {
//            System.out.println(download_model.getUrl());
            URL url = URLUtility.createURL(download_model.getUrl());
            File directory = new File("/Users/asiaserrano/ChromeDownloads", download_model.getFilename());
            String message = download_model.getUrl();
            if (download(url, directory, okHttpClient)) {
                System.out.println("SUCCESSFUL: " + message);
            } else {
                System.out.println("FAILURE: " + message);
            }
        }
    }

    private static SimsFindsModel getContinueURL(String content, SimsFindsModel model) {
        String key = StringUtility.regexBetween(content, "key=", "\"");
        String url = "https://www.simsfinds.com/continue?key=" + key;
        return new SimsFindsModel(CONTINUE, url, model);
    }

    private static SimsFindsModel getDownloadURL(String content, SimsFindsModel model) {
        String[] info = StringUtility.regexBetween(content, "data-at5t768r9=\"", SINGLE_QUOTE).split(COMMA);
        String flid = StringUtility.regexBetween(content, "data-at8r136r7=\"", SINGLE_QUOTE);
        String pass = StringUtility.regexBetween(content, "data-passe=\"", SINGLE_QUOTE);

        Map<String, String> map = new HashMap<>();
        map.put("cid", info[0]);
        map.put("key", info[1]);
        map.put("version", info[3]);
        map.put("pass", pass);
        map.put("flid", flid);

        List<String> list = map.entrySet().stream()
                .map(e -> String.format("%s=%s", e.getKey(), e.getValue()))
                .collect(Collectors.toList());

        String url = "https://click.simsfinds.com/download?" + String.join(AMPERSAND, list);
        return new SimsFindsModel(DOWNLOAD, url, model);
    }

//    private static String getExternalURL(String content) {
//        return StringUtility.regexBetween(content, "<title>", "</title");
//    }

    private static String getContent(SimsFindsModel model) {
        OkHttpClient okHttpClient = new OkHttpClient();

        Request request = new Request.Builder()
                .url(model.getUrl())
                .header("User-Agent", "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_15_7) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/131.0.0.0 Safari/537.36")
                .build();

        Call call = okHttpClient.newCall(request);

        try(Response response = call.execute()) {
            return response.body().string();
        } catch (Exception e) {
            return null;
        }

    }

    private static Response getResponse(URL url, OkHttpClient client) {
        Request request = new Request.Builder().url(url).build();
        Call call = client.newCall(request);
        try(Response response = call.execute()) {
            return response;
        } catch (Exception e) {
            return null;
        }
    }

}
