package ts4.helper.TS4Downloader.utilities;

import lombok.extern.slf4j.Slf4j;
import ts4.helper.TS4Downloader.enums.WebsiteEnum;
import ts4.helper.TS4Downloader.models.PatreonModel;

import java.io.File;
import java.io.FileOutputStream;
import java.net.URI;
import java.net.URL;
import java.nio.channels.Channels;
import java.nio.channels.FileChannel;
import java.nio.channels.ReadableByteChannel;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

@Slf4j
public abstract class URLUtility {

    public static URL createURL(String url) throws Exception {
        URI uri = new URI(url);
        return uri.toURL();
    }
    public static List<URL> createURLs(String[] strings) throws Exception {
        List<URL> urls = new ArrayList<>();
        for (String string: strings) urls.add(URLUtility.createURL(string));
        return urls;
    }

    public static boolean download(URL source, File destination) throws Exception {
        File directory = new File(destination.getParent());
        List<File> files = Arrays.asList(Objects.requireNonNull(directory.listFiles()));
        if (!files.contains(destination)) {
            try(FileOutputStream fileOutputStream = new FileOutputStream(destination)) {
                ReadableByteChannel readableByteChannel = Channels.newChannel(source.openStream());
                FileChannel fileChannel = fileOutputStream.getChannel();
                fileChannel.transferFrom(readableByteChannel, 0, Long.MAX_VALUE);
                log.info("downloaded url {} to {}", source, destination);
                if (UnzipUtility.isZipFile(destination)) UnzipUtility.unzip(destination);
                return true;
            } catch (Exception e) {
                log.error("unable to download url {} to {}", source, destination);
                return false;
            }
        } else {
            log.info("{} already exists", destination);
            return true;
        }
    }

//    public static WebsiteEnum getWebsite(URL url) {
//        for (WebsiteEnum website : WebsiteEnum.values())
//            if (url.toString().contains(website.url)) return website;
//        return null;
//    }

}
