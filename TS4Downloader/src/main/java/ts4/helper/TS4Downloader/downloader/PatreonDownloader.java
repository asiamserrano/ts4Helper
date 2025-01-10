package ts4.helper.TS4Downloader.downloader;

import ts4.helper.TS4Downloader.utilities.FileUtility;
import ts4.helper.TS4Downloader.utilities.StringUtility;
import ts4.helper.TS4Downloader.utilities.URLUtility;

import java.io.File;
import java.net.URL;
import java.util.regex.Matcher;

import static ts4.helper.TS4Downloader.constants.StringConstants.EMPTY;

public class PatreonDownloader {

    public static void main(String[] args) throws Exception {
        String location = "/Users/asiaserrano/ChromeDownloads";
        String content = StringUtility.loadResource("html_file.html");
        download(content, location);
    }

    public static boolean download(String content, String location) throws Exception {
        String directory_name = content.split("elementtiming=\"post-title\"")[1]
                .split(">")[1].split("<")[0];
        File directory = new File(location, directory_name);
        return FileUtility.createDirectory(directory) && download(content, directory);
    }

    private static boolean download(String content, File directory) throws Exception {
        Matcher matcher = StringUtility.getRegexBetweenMatcher(content, "\"download_url\":", "\"image_urls\"");
        String match;
        while (matcher.find()) {
            match = matcher.group();
            if (!match.contains("1.png")) {
                if (!download(directory, match)) {
                    return false;
                }
            }
        }
        return true;
    }

    private static boolean download(File directory, String match) throws Exception {
        URL source = getSource(match);
        File destination = getDestination(match, directory);
        return URLUtility.download(source, destination);
    }

    private static URL getSource(String match) throws Exception {
        String source_url = match.split("\",")[0]
                .replace("\"download_url\":\"", EMPTY)
                .replace("\\u0026token", "&token");
        return URLUtility.createURL(source_url);
    }

    private static File getDestination(String match, File directory) {
        String destination_file = match.split("\"file_name\":\"")[1]
                .split("\",\"")[0];
        return new File(directory, destination_file);
    }

}
