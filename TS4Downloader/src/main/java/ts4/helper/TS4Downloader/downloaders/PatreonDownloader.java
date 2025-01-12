package ts4.helper.TS4Downloader.downloaders;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ts4.helper.TS4Downloader.models.PatreonModel;
import ts4.helper.TS4Downloader.utilities.FileUtility;
import ts4.helper.TS4Downloader.utilities.StringUtility;
import ts4.helper.TS4Downloader.utilities.URLUtility;

import java.io.File;
import java.net.URL;
import java.util.HashSet;
import java.util.Set;
import java.util.regex.Matcher;

import static ts4.helper.TS4Downloader.constants.StringConstants.EMPTY;

@Slf4j
@Component
@AllArgsConstructor
public class PatreonDownloader implements Downloader {

    public static void main(String[] args) throws Exception {
        String location = "/Users/asiaserrano/ChromeDownloads";
        String content = StringUtility.loadResource("html_file.html");
        PatreonDownloader downloader = new PatreonDownloader();
        downloader.download(content, location);
    }

    public boolean download(String content, String location) throws Exception {
        String directory_name = content
                .split("elementtiming=\"post-title\"")[1]
                .split(">")[1]
                .split("<")[0];
        File directory = new File(location, directory_name);
        return FileUtility.createDirectory(directory) && download(content, directory);
    }

    private boolean download(String content, File directory) throws Exception {
        Set<PatreonModel> models = new HashSet<>();
        Matcher matcher = StringUtility.getRegexBetweenMatcher(content, "\"download_url\":", "\"image_urls\"");
        String match;
        while (matcher.find()) {
            match = matcher.group();
            if (!match.contains("1.png")) {
                createModel(directory, match, models);
            }
        }
        for (PatreonModel model: models) if(!URLUtility.download(model)) return false;
        return true;
    }

    private void createModel(File directory, String match, Set<PatreonModel> models) throws Exception {
        URL source = getSource(match);
        File destination = getDestination(match, directory);
        PatreonModel model = new PatreonModel(source, destination);
        models.add(model);
    }

    private URL getSource(String match) throws Exception {
        String source_url = match.split("\",")[0]
                .replace("\"download_url\":\"", EMPTY)
                .replace("\\u0026token", "&token");
        return URLUtility.createURL(source_url);
    }

    private File getDestination(String match, File directory) {
        String destination_file = match.split("\"file_name\":\"")[1]
                .split("\",\"")[0];
        return new File(directory, destination_file);
    }

}
