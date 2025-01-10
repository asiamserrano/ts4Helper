package ts4.helper.TS4Downloader.downloader;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ts4.helper.TS4Downloader.utilities.FileUtility;
import ts4.helper.TS4Downloader.utilities.StringUtility;
import ts4.helper.TS4Downloader.utilities.URLUtility;

import java.io.File;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URL;
import java.net.URLConnection;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

//import static org.example.Constants.USER_AGENT_KEY;
//import static org.example.Constants.USER_AGENT_VALUE;
//import static org.example.Utilities.*;
import static ts4.helper.TS4Downloader.constants.StringConstants.SINGLE_QUOTE;
import static ts4.helper.TS4Downloader.constants.StringConstants.COMMA;


public class CurseForge {

    private static final Logger log = LoggerFactory.getLogger(CurseForge.class);

    // https://www.curseforge.com/sims4/create-a-sim/goldfish-spring-breath-dress
    // https://www.curseforge.com/api/v1/mods/1008775/files/6029686/download

    private static final String DOWNLOAD_URL = "https://www.curseforge.com/api/v1/mods/%s/files/%s/download";

    public static void main(String[] args) throws Exception {
        String location = "/Users/asiaserrano/ChromeDownloads";
        String content = FileUtility.getFileContentString(FileUtility.getFile("html_file.html"));
        download(content, location);
    }

    public static boolean download(String content, String location) throws Exception {
        String id1 = StringUtility.regexBetween(content, "\"identifier\":\"", SINGLE_QUOTE);
        String id2 = StringUtility.regexBetween(content, "\"mainFile\":{\"id\":", COMMA);
        String file_name = StringUtility.regexBetween(content, "\"fileName\":\"", SINGLE_QUOTE);
        String folder_name = file_name.split("\\.")[0];

        File directory = Paths.get(location, folder_name).toFile();
        File file = new File(directory, file_name);
        String actual = StringUtility.format(DOWNLOAD_URL, id1, id2);

        return FileUtility.createDirectory(directory) && download(file, actual);
    }

    private static boolean download(File file, String actual) throws Exception {
        URI uri = new URI(actual);
        URL url = uri.toURL();
        return URLUtility.download(url, file);
    }

}
