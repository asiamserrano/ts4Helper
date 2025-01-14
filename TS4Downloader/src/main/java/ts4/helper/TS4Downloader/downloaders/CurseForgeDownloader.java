//package ts4.helper.TS4Downloader.downloaders;
//
//import lombok.extern.slf4j.Slf4j;
//import okhttp3.OkHttpClient;
//import org.springframework.stereotype.Component;
//import ts4.helper.TS4Downloader.models.DownloadResponse;
//import ts4.helper.TS4Downloader.utilities.FileUtility;
//import ts4.helper.TS4Downloader.utilities.StringUtility;
//import ts4.helper.TS4Downloader.utilities.URLUtility;
//
//import java.io.File;
//import java.net.URL;
//import java.util.HashSet;
//import java.util.List;
//import java.util.Set;
//import java.util.regex.Matcher;
//
//import static ts4.helper.TS4Downloader.constants.StringConstants.*;
//import static ts4.helper.TS4Downloader.constants.StringConstants.EMPTY;
//
//import static ts4.helper.TS4Downloader.enums.WebsiteEnum.CURSE_FORGE;
//
//@Slf4j
//@Component
//public class CurseForgeDownloader extends DownloaderImpl {
//
//    /*
//    https://www.curseforge.com/sims4/create-a-sim/goldfish-spring-breath-dress
//    https://www.curseforge.com/api/v1/mods/1008775/files/6029686/download
//    */
//
//    private static final String DOWNLOAD_URL = "https://www.curseforge.com/api/v1/mods/%s/files/%s/download";
//
//    public CurseForgeDownloader(OkHttpClient client) {
//        super(client);
//    }
//
//    public static void main(String[] args) throws Exception {
////        File starting_directory = new File("/Users/asiaserrano/zzz");
//        String content = StringUtility.loadResource("html_file.html");
////        CurseForgeDownloader downloader = new CurseForgeDownloader(new OkHttpClient());
////        downloader.fo(content, location);
//
//        List<String> links = StringUtility.getSetBetweenRegex(content, "btn-cta\" href=\"/", "\">").stream()
//                .filter(str -> str.contains("download"))
//                .map(str -> CURSE_FORGE.httpUrl.toString() + str)
//                .toList();
//
//        for (String str: links) System.out.println(str);
//
////        Matcher matcher = StringUtility.getRegexBetweenMatcher(content, prefix, suffix);
////        Set<String> set = new HashSet<>();
////        while (matcher.find()) {
////            String match = matcher.group();
////            if (match.contains("download")) {
////                //https://www.curseforge.com/sims4/create-a-sim/celine-halter-tank/download/5639469
////                match = match.replace(suffix, EMPTY).replace(prefix, EMPTY);
////                System.out.println(CURSE_FORGE.httpUrl.toString() + match);
////            }
//////                    .replace(prefix, EMPTY)
//////                    .replace(suffix, EMPTY)
//////                    .replaceAll("\\\\", EMPTY);
//////            set.add(match);
////        }
//
//    }
//
//    public DownloadResponse download(URL url, File starting_directory) throws Exception {
//        String content = getURLContent(url);
//        boolean bool;
//        if (content.contains("Just a moment...")) {
//            bool = false;
//        } else {
//            String file_name = StringUtility.getStringBetweenRegex(content, "\"fileName\":\"", SINGLE_QUOTE);
//            File directory = getDirectory(starting_directory, file_name);
//            File destination = new File(directory, file_name);
//            URL source = getSource(content);
//            bool = FileUtility.createDirectory(directory) && URLUtility.download(source, destination);
//        }
//
//        return new DownloadResponse(bool, url);
//    }
//
//    private URL getSource(String content) throws Exception {
//        String id1 = StringUtility.getStringBetweenRegex(content, "\"identifier\":\"", SINGLE_QUOTE);
//        String id2 = StringUtility.getStringBetweenRegex(content, "\"mainFile\":{\"id\":", COMMA);
//        String source_url = String.format(DOWNLOAD_URL, id1, id2);
//        return URLUtility.createURL(source_url);
//    }
//
//    private File getDirectory(File location, String file_name) throws Exception {
//        String folder_name = file_name.split("\\.")[0];
//        return new File(location, folder_name);
//    }
//
//}
