package ts4.helper.TS4Downloader.utilities;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ts4.helper.TS4Downloader.controller.EventController;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

import static ts4.helper.TS4Downloader.constants.StringConstants.EMPTY;

public abstract class FileUtility {

    private static final Logger log = LoggerFactory.getLogger(FileUtility.class);

//    public static void deleteFile(File theFile) {
//        if (!theFile.delete()) {
//            printErrorMessage(UNABLE_TO_DELETE, theFile);
//        }
//    }

    public static File getFile(String file) throws Exception {
        URL url = URLUtility.loadURLFile(file);
        return new File(url.toURI());
    }

    public static List<String> getFileContentList(File file) throws IOException {
        return Files.readAllLines(Paths.get(file.getAbsolutePath()));
    }

    public static String getFileContentString(File file) throws IOException {
        List<String> result = getFileContentList(file);
        return String.join(EMPTY, result);
    }

    public static boolean createDirectory(File directory) {
        String folder_name = directory.getAbsolutePath();
        if (!directory.exists()) {
            if (directory.mkdir()) {
                log.info("folder created for {}", folder_name);
                return true;
            } else {
                log.info("folder cannot be created for {}", folder_name);
                return false;
            }
        } else {
            log.info("existing folder for {}", folder_name);
            return true;
        }
    }

}
