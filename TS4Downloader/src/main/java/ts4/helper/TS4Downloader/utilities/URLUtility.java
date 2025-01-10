package ts4.helper.TS4Downloader.utilities;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLConnection;
import java.nio.channels.Channels;
import java.nio.channels.FileChannel;
import java.nio.channels.ReadableByteChannel;
import java.nio.file.Path;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public abstract class URLUtility {

    private static final Logger log = LoggerFactory.getLogger(URLUtility.class);

    public static URL loadURLFile(String file) {
        URL resource = URLUtility.class.getClassLoader().getResource(file);
        if (resource == null) {
            throw new IllegalArgumentException("file not found!");
        } else {
            return resource;
        }
    }

//    public static boolean download(URL url, Path file_path) {
//        String location = file_path.toString();
//        return download(url, location);
//    }

    public static boolean download(URL url, File file) {
        String location = file.getAbsolutePath();
        return download(url, location);
    }

    private static boolean download(URL url, String file_location) {
        try(FileOutputStream fileOutputStream = new FileOutputStream(file_location)) {
            ReadableByteChannel readableByteChannel = Channels.newChannel(url.openStream());
            FileChannel fileChannel = fileOutputStream.getChannel();
            fileChannel.transferFrom(readableByteChannel, 0, Long.MAX_VALUE);
            return true;
        } catch (Exception e) {
            log.error("unable to download url {} to {}", url, file_location);
            return false;
        }
    }

//    public static List<String> getURLContentList(URL url) throws IOException {
//        return getURLContent(url).collect(Collectors.toList());
//    }
//
//    public static String getURLContentString(URL url) throws Exception {
//        return getURLContent(url).collect(Collectors.joining(EMPTY));
//    }
//
//    private static Stream<String> getURLContent(URL url) throws IOException {
//        URLConnection openConnection = getURLConnection(url);
//        String redirect = openConnection.getHeaderField("Location");
//        if (redirect != null){
//            openConnection = getURLConnection(new URL(redirect));
//        }
//        BufferedReader r = new BufferedReader(new InputStreamReader(openConnection.getInputStream()));
//        return r.lines();
//    }

}
