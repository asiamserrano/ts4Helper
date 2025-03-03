package org.projects.ts4.utility.utilities;

import lombok.extern.slf4j.Slf4j;
import org.projects.ts4.avro.WebsiteModel;

import java.net.URI;
import java.net.URL;

@Slf4j
public abstract class URLUtility {

    public static URL createURLException(String url) throws Exception {
        return new URI(url).toURL();
    }

    public static URL createURLNoException(String url) {
        try {
            return createURLException(url);
        } catch (Exception e) {
            log.error("unable to create URL for {}", url, e);
            return null;
        }
    }

    public static URL createURL(WebsiteModel websiteModel) {
        return createURLNoException(websiteModel.getUrl());
    }


//    public static boolean download(URL source, File destination) {
//        File directory = new File(destination.getParent());
//        List<File> files = Arrays.asList(Objects.requireNonNull(directory.listFiles()));
//        if (!files.contains(destination)) {
//            try(FileOutputStream fileOutputStream = new FileOutputStream(destination)) {
//                ReadableByteChannel readableByteChannel = Channels.newChannel(source.openStream());
//                FileChannel fileChannel = fileOutputStream.getChannel();
//                fileChannel.transferFrom(readableByteChannel, 0, Long.MAX_VALUE);
//                log.info("downloaded url {} to {}", source, destination);
//                if (UnzipUtility.isZipFile(destination)) UnzipUtility.unzip(destination);
//                return true;
//            } catch (Exception e) {
//                log.error("unable to download url {} to {}", source, destination, e);
//                return false;
//            }
//        } else {
//            log.info("{} already exists", destination);
//            return true;
//        }
//    }

}
