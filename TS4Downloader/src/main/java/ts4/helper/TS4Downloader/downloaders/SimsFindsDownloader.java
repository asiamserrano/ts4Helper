//package ts4.helper.TS4Downloader.downloaders;
//
//import lombok.extern.slf4j.Slf4j;
//import okhttp3.OkHttpClient;
//import okhttp3.Response;
//import org.springframework.stereotype.Component;
//import ts4.helper.TS4Downloader.enums.ExtensionEnum;
//import ts4.helper.TS4Downloader.models.DownloadResponse;
//import ts4.helper.TS4Downloader.models.SimsFindsModel;
//import ts4.helper.TS4Downloader.utilities.FileUtility;
//import ts4.helper.TS4Downloader.utilities.OkHttpUtility;
//import ts4.helper.TS4Downloader.utilities.StringUtility;
//import ts4.helper.TS4Downloader.utilities.URLUtility;
//
//import java.io.File;
//import java.net.URL;
//import java.util.HashMap;
//import java.util.List;
//import java.util.Map;
//import java.util.stream.Collectors;
//
//import static ts4.helper.TS4Downloader.constants.StringConstants.SINGLE_QUOTE;
//import static ts4.helper.TS4Downloader.constants.StringConstants.COMMA;
//import static ts4.helper.TS4Downloader.constants.StringConstants.AMPERSAND;
//import static ts4.helper.TS4Downloader.enums.SimsFindsEnum.DOWNLOADS;
//import static ts4.helper.TS4Downloader.enums.SimsFindsEnum.CONTINUE;
//import static ts4.helper.TS4Downloader.enums.SimsFindsEnum.DOWNLOAD;
//
//@Slf4j
//@Component
//public class SimsFindsDownloader extends DownloaderImpl {
//
//    public SimsFindsDownloader(OkHttpClient client) {
//        super(client);
//    }
//
//    // External Link
//    // https://www.simsfinds.com/downloads/319350/snowflake-boots-sims4
//    // https://www.simsfinds.com/continue?key=f987fc317309e21ce0fdadaaa140c47e
//    // https://click.simsfinds.com/download?flid=0&pass=2307102333&version=1675093910&key=f987fc317309e21ce0fdadaaa140c47e&cid=319350
//
//    // Direct Link
//    // https://www.simsfinds.com/downloads/316599/la-medusa-plataform-sandals-sims4
//    // https://www.simsfinds.com/continue?key=9e4a77dd0c64425f1da52a38303d83e3
//    // https://click.simsfinds.com/download?flid=173321671320709&pass=2307102333&version=1733216713&key=9e4a77dd0c64425f1da52a38303d83e3&cid=316599
//
//    public static void main(String[] args) throws Exception {
//        File starting_directory = new File("/Users/asiaserrano/zzz");
//        SimsFindsDownloader downloader = new SimsFindsDownloader(new OkHttpClient());
////        URL url = URLUtility.createURL("https://www.simsfinds.com/downloads/316599/la-medusa-plataform-sandals-sims4");
//        URL url = URLUtility.createURL("https://www.simsfinds.com/downloads/319350/snowflake-boots-sims4");
//        DownloadResponse downloadResponse = downloader.download(url, starting_directory);
//        log.info("response: {}", downloadResponse);
//    }
//
//    public DownloadResponse download(URL url, File starting_directory) throws Exception {
//        String content = getURLContent(url);
//        String url_str = StringUtility.getStringBetweenRegex(content, "\"og:url\" content=\"", SINGLE_QUOTE);
//        URL newURL = URLUtility.createURL(url_str);
//        SimsFindsModel model = new SimsFindsModel(DOWNLOADS, newURL);
//        return download(model, starting_directory);
//    }
//
//    private DownloadResponse download(SimsFindsModel downloads_model, File location) throws Exception {
//        String downloads_content = getContent(downloads_model);
//        SimsFindsModel continue_model = getContinueURL(downloads_content, downloads_model);
//        String continue_content = getContent(continue_model);
//        SimsFindsModel download_model = getDownloadURL(continue_content, continue_model);
//        if (download_model.urlContains("flid=0")) {
//            String download_content = getContent(download_model);
//            String external_url = StringUtility.getStringBetweenRegex(download_content, "<title>", "</title");
////            log.info("continue_model url: {}", continue_model.url);
////            log.info("download_model url: {}", download_model.url);
////            log.info("simsfinds external url: {}", external_url);
//
//            if (external_url.isEmpty()) {
//                return new DownloadResponse(false, downloads_model.url);
//            } else {
//                return new DownloadResponse(URLUtility.createURL(external_url));
//            }
//        } else {
////            URL url = URLUtility.createURL(download_model.url);
//            URL url = download_model.url;
//            File directory = new File(location, download_model.filename);
//            boolean bool = download(directory, url);
//            return new DownloadResponse(bool, url);
//        }
//    }
//
//    private boolean download(File directory, URL source) throws Exception {
//        Response response = OkHttpUtility.sendRequest(source, this.client);
//        ExtensionEnum extensionEnum = ExtensionEnum.get(response);
//        response.close();
//        if (extensionEnum == null) return false;
//        String file_name = directory.getName();
//        File destination = new File(directory, file_name + extensionEnum.extension);
//        return FileUtility.createDirectory(directory) && URLUtility.download(source, destination);
//    }
//
//    private SimsFindsModel getContinueURL(String content, SimsFindsModel model) throws Exception {
//        String key = StringUtility.getStringBetweenRegex(content, "key=", SINGLE_QUOTE);
//        URL url = URLUtility.createURL("https://www.simsfinds.com/continue?key=" + key);
//        return new SimsFindsModel(CONTINUE, url, model);
//    }
//
//    private SimsFindsModel getDownloadURL(String content, SimsFindsModel model) throws Exception {
//        String[] info = StringUtility.getStringBetweenRegex(content, "data-at5t768r9=\"", SINGLE_QUOTE).split(COMMA);
//        String flid = StringUtility.getStringBetweenRegex(content, "data-at8r136r7=\"", SINGLE_QUOTE);
//        String pass = StringUtility.getStringBetweenRegex(content, "data-passe=\"", SINGLE_QUOTE);
//
//        Map<String, String> map = new HashMap<>();
//        map.put("cid", info[0]);
//        map.put("key", info[1]);
//        map.put("version", info[3]);
//        map.put("pass", pass);
//        map.put("flid", flid);
//
//        List<String> list = map.entrySet().stream()
//                .map(e -> String.format("%s=%s", e.getKey(), e.getValue()))
//                .collect(Collectors.toList());
//
////        String url = "https://click.simsfinds.com/download?" + String.join(AMPERSAND, list);
//        URL url = URLUtility.createURL("https://click.simsfinds.com/download?" + String.join(AMPERSAND, list));
//        return new SimsFindsModel(DOWNLOAD, url, model);
//    }
//
//    private String getContent(SimsFindsModel model) throws Exception {
//        URL url = model.url;
//        return getURLContent(url);
//    }
//
//}
