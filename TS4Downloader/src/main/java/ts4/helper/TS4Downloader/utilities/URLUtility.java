package ts4.helper.TS4Downloader.utilities;

import lombok.extern.slf4j.Slf4j;
import ts4.helper.TS4Downloader.models.PatreonModel;

import java.io.File;
import java.io.FileOutputStream;
import java.net.URI;
import java.net.URL;
import java.nio.channels.Channels;
import java.nio.channels.FileChannel;
import java.nio.channels.ReadableByteChannel;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

@Slf4j
public abstract class URLUtility {

//    public static URL loadURLFile(String file) {
//        URL resource = URLUtility.class.getClassLoader().getResource(file);
//        if (resource == null) {
//            throw new IllegalArgumentException("file not found!");
//        } else {
//            return resource;
//        }
//    }

//    public static boolean download(URL url, Path file_path) {
//        String location = file_path.toString();
//        return download(url, location);
//    }

    public static URL createURL(String url) throws Exception {
        URI uri = new URI(url);
        return uri.toURL();
    }

    public static String getURLString(URL url) {
        String host = url.getHost();
        String path = url.getPath();
        return String.format("%s%s", host, path);
    }

//    private static boolean download(File directory, File destination, URL source) throws Exception {
//        List<String> filenames = FileUtility.getDirectoryFilenames(directory);
//        String destination_file = destination.getName();
//        if (!filenames.contains(destination_file)) {
//            return URLUtility.download(source, destination);
//        } else {
//            log.info("{} already exists", destination_file);
//            return true;
//        }
//    }

    public static boolean download(PatreonModel model) throws Exception {
        URL source = model.source;
        File destination = model.destination;
        return download(source, destination);
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
