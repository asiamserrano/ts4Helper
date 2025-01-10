package ts4.helper.TS4Downloader.utilities;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

public abstract class FileUtility {

    private static final Logger log = LoggerFactory.getLogger(FileUtility.class);

    public static boolean createDirectory(File directory) {
        if (!directory.exists()) {
            if (directory.mkdir()) {
                log.info("folder created for {}", directory);
                return true;
            } else {
                log.info("folder cannot be created for {}", directory);
                return false;
            }
        } else {
            log.info("existing folder for {}", directory);
            return true;
        }
    }

    public static List<String> getDirectoryFilenames(File directory) throws Exception {
        if (directory.isDirectory()) {
            return Arrays.stream(Objects.requireNonNull(directory.listFiles())).
                    map(File::getName)
                    .toList();
        } else {
            log.error("{} is not a directory", directory);
            throw new Exception("file is not directory");
        }
    }

    //    public static void deleteFile(File theFile) {
//        if (!theFile.delete()) {
//            printErrorMessage(UNABLE_TO_DELETE, theFile);
//        }
//    }

}
