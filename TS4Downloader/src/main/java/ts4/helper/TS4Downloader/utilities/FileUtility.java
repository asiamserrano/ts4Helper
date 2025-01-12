package ts4.helper.TS4Downloader.utilities;

import lombok.extern.slf4j.Slf4j;

import java.io.File;

@Slf4j
public abstract class FileUtility {

    public static boolean createDirectory(File directory) {
        if (!directory.exists()) {
            if (directory.mkdirs()) {
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

//    public static boolean createDirectory(File directory) {
//        if (directory.isDirectory()) {
//            if (!directory.exists()) {
//                if (directory.mkdirs()) {
//                    log.info("folder created for {}", directory);
//                    return true;
//                } else {
//                    log.info("folder cannot be created for {}", directory);
//                    return false;
//                }
//            } else {
//                log.info("existing folder for {}", directory);
//                return true;
//            }
//        } else {
//            log.error("file is not a directory: {}", directory);
//            return false;
//        }
//    }

//    public static List<String> getDirectoryFilenames(File directory) throws Exception {
//        if (directory.isDirectory()) {
//            return Arrays.stream(Objects.requireNonNull(directory.listFiles())).
//                    map(File::getName)
//                    .toList();
//        } else {
//            log.error("{} is not a directory", directory);
//            throw new Exception("file is not directory");
//        }
//    }


        public static void deleteFile(File theFile) {
        if (!theFile.delete()) {
            log.error("unable to delete file: {}", theFile);
//            printErrorMessage(UNABLE_TO_DELETE, theFile);
        }
    }

}
