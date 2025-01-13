package ts4.helper.TS4Downloader.downloaders;

import com.fasterxml.jackson.databind.deser.DataFormatReaders;
import lombok.extern.slf4j.Slf4j;
import okhttp3.OkHttpClient;
import org.springframework.stereotype.Component;
import ts4.helper.TS4Downloader.models.DownloadResponse;
import ts4.helper.TS4Downloader.models.PatreonModel;
import ts4.helper.TS4Downloader.utilities.FileUtility;
import ts4.helper.TS4Downloader.utilities.StringUtility;
import ts4.helper.TS4Downloader.utilities.URLUtility;

import java.io.File;
import java.net.URL;
import java.util.*;
import java.util.regex.Matcher;

import static ts4.helper.TS4Downloader.constants.StringConstants.EMPTY;

@Slf4j
@Component
public class PatreonDownloader extends DownloaderImpl {

    public PatreonDownloader(OkHttpClient client) {
        super(client);
    }

    public static void main(String[] args) throws Exception {
        File starting_directory = new File("/Users/asiaserrano/zzz");

        PatreonDownloader downloader = new PatreonDownloader(new OkHttpClient());

        List<String> strings = Arrays.asList("https://www.patreon.com/posts/wire-high-heels-113597759",
                "https://www.patreon.com/posts/dallz-high-hells-108322493");

        for (String string: strings) {
            URL url = URLUtility.createURL(string);
            downloader.download(url, starting_directory);
            log.info("----------");
        }

    }

    public DownloadResponse download(URL url, File starting_directory) throws Exception {
        String content = getURLContent(url);
        String directory_name = content
                .split("elementtiming=\"post-title\"")[1]
                .split(">")[1]
                .split("<")[0];
        File directory = new File(starting_directory, directory_name);
        boolean result = FileUtility.createDirectory(directory) && download(content, directory);
        return new DownloadResponse(result, url);
    }

    private boolean download(String content, File directory) throws Exception {
        Set<PatreonModel> models = new HashSet<>();
        Matcher matcher = StringUtility.getRegexBetweenMatcher(content, "\"download_url\":", "\"image_urls\"");
        String match;
        while (matcher.find()) {
            match = matcher.group();
            if (!match.contains("1.png") && !match.contains("1.jpg")) {
                models.add(createModel(directory, match));
            }
        }
        for (PatreonModel model: models) if(!URLUtility.download(model.source, model.destination)) return false;
        return !models.isEmpty();
    }

    private PatreonModel createModel(File directory, String match) throws Exception {
        URL source = getSource(match);
        File destination = getDestination(match, directory);
        return new PatreonModel(source, destination);
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
