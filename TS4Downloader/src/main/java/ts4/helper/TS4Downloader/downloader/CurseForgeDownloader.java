package ts4.helper.TS4Downloader.downloader;

import ts4.helper.TS4Downloader.utilities.FileUtility;
import ts4.helper.TS4Downloader.utilities.StringUtility;
import ts4.helper.TS4Downloader.utilities.URLUtility;

import java.io.File;
import java.net.URL;

import static ts4.helper.TS4Downloader.constants.StringConstants.SINGLE_QUOTE;
import static ts4.helper.TS4Downloader.constants.StringConstants.COMMA;

public class CurseForgeDownloader {

    /*
    https://www.curseforge.com/sims4/create-a-sim/goldfish-spring-breath-dress
    https://www.curseforge.com/api/v1/mods/1008775/files/6029686/download
    */

    private static final String DOWNLOAD_URL = "https://www.curseforge.com/api/v1/mods/%s/files/%s/download";

    public static void main(String[] args) throws Exception {
        String location = "/Users/asiaserrano/ChromeDownloads";
        String content = StringUtility.loadResource("html_file.html");
        download(content, location);
    }

    public static boolean download(String content, String location) throws Exception {
        String file_name = StringUtility.regexBetween(content, "\"fileName\":\"", SINGLE_QUOTE);
        File directory = getDirectory(location, file_name);
        File destination = new File(directory, file_name);
        URL source = getSource(content);
        return FileUtility.createDirectory(directory) && URLUtility.download(source, destination);
    }

    private static URL getSource(String content) throws Exception {
        String id1 = StringUtility.regexBetween(content, "\"identifier\":\"", SINGLE_QUOTE);
        String id2 = StringUtility.regexBetween(content, "\"mainFile\":{\"id\":", COMMA);
        String source_url = String.format(DOWNLOAD_URL, id1, id2);
        return URLUtility.createURL(source_url);
    }

    private static File getDirectory(String location, String file_name) throws Exception {
        String folder_name = file_name.split("\\.")[0];
        return new File(location, folder_name);
    }

}
