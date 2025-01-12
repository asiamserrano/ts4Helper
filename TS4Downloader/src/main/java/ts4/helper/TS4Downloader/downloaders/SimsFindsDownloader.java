package ts4.helper.TS4Downloader.downloaders;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import okhttp3.OkHttpClient;
import okhttp3.Response;
import okhttp3.Request;
import okhttp3.Call;
import org.springframework.stereotype.Component;
import ts4.helper.TS4Downloader.enums.ExtensionEnum;
import ts4.helper.TS4Downloader.models.SimsFindsModel;
import ts4.helper.TS4Downloader.utilities.FileUtility;
import ts4.helper.TS4Downloader.utilities.OkHttpUtility;
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
import static ts4.helper.TS4Downloader.enums.WebsiteEnum.SIMS_FINDS;
import static ts4.helper.TS4Downloader.enums.SimsFindsEnum.DOWNLOADS;
import static ts4.helper.TS4Downloader.enums.SimsFindsEnum.CONTINUE;
import static ts4.helper.TS4Downloader.enums.SimsFindsEnum.DOWNLOAD;

@Slf4j
@Component
@AllArgsConstructor
public class SimsFindsDownloader implements Downloader {

    // External Link
    // https://www.simsfinds.com/downloads/319350/snowflake-boots-sims4
    // https://www.simsfinds.com/continue?key=f987fc317309e21ce0fdadaaa140c47e
    // https://click.simsfinds.com/download?flid=0&pass=2307102333&version=1675093910&key=f987fc317309e21ce0fdadaaa140c47e&cid=319350

    // Direct Link
    // https://www.simsfinds.com/downloads/316599/la-medusa-plataform-sandals-sims4
    // https://www.simsfinds.com/continue?key=9e4a77dd0c64425f1da52a38303d83e3
    // https://click.simsfinds.com/download?flid=173321671320709&pass=2307102333&version=1733216713&key=9e4a77dd0c64425f1da52a38303d83e3&cid=316599

//    private static final Path SIMS_FINDS_PATH = Paths.get(SIMS_FINDS.url, DOWNLOADS.url_delimiter);

    private final OkHttpClient client;

    public static void main(String[] args) throws Exception {
        String location = "/Users/asiaserrano/ChromeDownloads";
        OkHttpClient okHttpClient = new OkHttpClient();
        SimsFindsDownloader downloader = new SimsFindsDownloader(okHttpClient);
        String url = "https://www.simsfinds.com/downloads/316599/la-medusa-plataform-sandals-sims4";
//        String url = "https://www.simsfinds.com/downloads/319350/snowflake-boots-sims4";
        SimsFindsModel model = new SimsFindsModel(DOWNLOADS, url);
        String content = downloader.getContent(model);
        downloader.download(content, location);
    }

//    private void parse(String string) {
//        if (string.contains(SIMS_FINDS_PATH.toString())) {
//            String url = string.split(SINGLE_QUOTE)[1];
//            try {
//                SimsFindsModel model = new SimsFindsModel(DOWNLOADS, url);
//                System.out.println(model.filename);
//                System.out.println(model.url);
//                System.out.println(model.simsFindsEnum.url_delimiter);
////                download(model, client);
//            } catch (Exception e) {
//                log.error("could not parse string: {}", string);
//            }
//        }
//    }

    public boolean download(String content, String location) throws Exception {
        String url = StringUtility.regexBetween(content, "\"og:url\" content=\"", SINGLE_QUOTE);
        SimsFindsModel model = new SimsFindsModel(DOWNLOADS, url);
        File directory = new File(location);
        download(model, directory);
        return false;
    }

    private void download(SimsFindsModel downloads_model, File location) throws Exception {
        String downloads_content = getContent(downloads_model);
        SimsFindsModel continue_model = getContinueURL(downloads_content, downloads_model);
        String continue_content = getContent(continue_model);
        SimsFindsModel download_model = getDownloadURL(continue_content, continue_model);
        if (download_model.urlContains("flid=0")) {
            String download_content = getContent(download_model);
            String external_url = StringUtility.regexBetween(download_content, "<title>", "</title");
            System.out.println(external_url);
        } else {
            URL url = URLUtility.createURL(download_model.url);
            File directory = new File(location, download_model.filename);
            String message = download_model.url;
            if (download(url, directory)) {
                System.out.println("SUCCESSFUL: " + message);
            } else {
                System.out.println("FAILURE: " + message);
            }
        }
    }

    private boolean download(URL source, File directory) throws Exception {
        Response response = OkHttpUtility.sendRequest(source, client);
        ExtensionEnum extensionEnum = ExtensionEnum.get(response);
        String file_name = directory.getName();
        File destination = new File(directory, file_name + extensionEnum.extension);
        return FileUtility.createDirectory(directory) && URLUtility.download(source, destination);
    }

    private SimsFindsModel getContinueURL(String content, SimsFindsModel model) {
        String key = StringUtility.regexBetween(content, "key=", SINGLE_QUOTE);
        String url = "https://www.simsfinds.com/continue?key=" + key;
        return new SimsFindsModel(CONTINUE, url, model);
    }

    private SimsFindsModel getDownloadURL(String content, SimsFindsModel model) {
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

    private String getContent(SimsFindsModel model) throws Exception {
        Response response = OkHttpUtility.sendRequest(model.url, client);
        return response.body().string();
    }

}
