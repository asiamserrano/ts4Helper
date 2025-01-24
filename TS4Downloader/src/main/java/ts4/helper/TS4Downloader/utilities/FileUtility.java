package ts4.helper.TS4Downloader.utilities;

import com.google.common.io.Resources;
import lombok.extern.slf4j.Slf4j;
import org.json.simple.JSONObject;
import ts4.helper.TS4Downloader.models.DownloadResponse;

import java.io.File;
import java.io.FileWriter;
import java.net.URL;
import java.util.Objects;

import static ts4.helper.TS4Downloader.constants.StringConstants.*;
import static ts4.helper.TS4Downloader.constants.StringConstants.EMPTY;
import static ts4.helper.TS4Downloader.enums.ExtensionEnum.PACKAGE;

@Slf4j
public abstract class FileUtility {

    public static void main(String[] args) throws Exception {

        URL url = URLUtility.createURL("https://www.baeldung.com/thread-pool-java-and-guava");
        URL contentURL = Resources.getResource("sample.txt");
        File file = new File(contentURL.getFile());
        FileUtility.writeToFile(file, "HI!", true);
    }

    public static void writeToFile(File file, JSONObject jsonObject, boolean append) {
        String jsonString = jsonObject.toJSONString().replaceAll(BACK_SLASHES, EMPTY);
        writeToFile(file, jsonString, append);
    }

    public static void writeToFile(File f, String string, boolean append) {
        File file = new File(f.getAbsolutePath().replace("target/classes", "src/main/resources"));
        try {
            if (!file.exists()) file.createNewFile();
            FileWriter fileWriter = new FileWriter(file, append);
            fileWriter.write(string);
            fileWriter.write(NEW_LINE);
            fileWriter.close();
        } catch (Exception e) {
            log.error("unable to write to file: {}", file, e);
            throw new RuntimeException(e);
        }
    }

    public static void deleteNonPackageFiles(File directory) {
        String filename;
        for (File file : Objects.requireNonNull(directory.listFiles())) {
            filename = file.getName();
            if (!filename.contains(PACKAGE.extension) && !filename.equals(DS_Store)) {
                log.info("deleting {}", file);
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
        if (theFile.delete()) {
            log.info("file deleted: {}", theFile);
        } else {
            log.error("unable to delete file: {}", theFile);
        }
    }

}
