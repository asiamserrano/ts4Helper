package ts4.helper.TS4Downloader.utilities;

import lombok.extern.slf4j.Slf4j;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

import static ts4.helper.TS4Downloader.enums.ExtensionEnum.PACKAGE;
import static ts4.helper.TS4Downloader.constants.StringConstants.DS_Store;
import static ts4.helper.TS4Downloader.constants.StringConstants.NEW_LINE;

@Slf4j
public abstract class FileUtility {

    public static void main(String[] args) throws Exception {

    }

    public static void writeToFile(File file, String string, boolean append) throws Exception {
        FileWriter fileWriter = new FileWriter(file, append);
        fileWriter.write(string);
        fileWriter.write(NEW_LINE);
        fileWriter.close();
    }

    public static void deleteNonPackageFiles(File directory) {
        String filename;
        for (File file : Objects.requireNonNull(directory.listFiles())) {
            filename = file.getName();
            if (!filename.contains(PACKAGE.extension) && !filename.equals(DS_Store)) {
                deleteFile(file);
            }
        }
    }

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
        }
    }

}
