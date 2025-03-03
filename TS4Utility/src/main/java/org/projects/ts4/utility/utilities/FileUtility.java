package org.projects.ts4.utility.utilities;

import lombok.extern.slf4j.Slf4j;
import org.projects.ts4.avro.WebsiteModel;
import org.projects.ts4.utility.constants.StringConstants;
import org.projects.ts4.utility.enums.ResponseEnum;

import java.io.File;
import java.io.FileWriter;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

@Slf4j
public class FileUtility {

    public static File createFile(File directory, ResponseEnum response) {
        String filename = response.name() + ".txt";
        return FileUtility.createFile(directory.getAbsolutePath(), filename);
    }

    private static File create(String... strings) {
        return new File(String.join(File.separator, strings));
    }

    public static File createDirectory(String... strings) {
        File file = create(strings);
        return createDirectory(file);
    }

    public static File createDirectory(File file) {
        if (file.exists()) {
            return file;
        } else {
            if (file.mkdirs()) {
                return file;
            } else {
                log.error("unable to create directory: {}", file);
            }
        }
        return null;
    }

    public static File createFile(WebsiteModel websiteModel) {
        String directory = websiteModel.getDirectory();
        String filename = websiteModel.getFilename();
        return createFile(directory, filename);
    }

    public static File createFile(String... strings) {
        File file = create(strings);
        if (file.exists()) {
            return setExecutable(file);
        } else {
            File parent = createDirectory(file.getParentFile());
            if (parent != null) {
                try {
                    if (file.createNewFile()) {
                        return setExecutable(file);
                    } else {
                        log.error("unable to create file: {}", file);
                    }
                } catch (Exception e) {
                    log.error("exception in creating file: {}", e.getMessage());
                }
            }
        }
        return null;
    }

    private static File setExecutable(File file) {
        if (file.getAbsolutePath().endsWith(".sh")) {
            file.setExecutable(true);
        }
        return file;
    }

    public static void write(File file, String string) {
        try(FileWriter fileWriter = new FileWriter(file, true)) {
            fileWriter.write(string);
            fileWriter.write(StringConstants.NEW_LINE);
        } catch (Exception e) {
            log.error("unable to create filewriter: {}", e.getMessage());
        }
    }

    public static void write(File file, WebsiteModel websiteModel) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss:SSS");
        String time = ZonedDateTime.now().format(formatter);
        String write = String.format("%-25s%s", time, websiteModel);
        write(file, write);
    }

    public static void consolidate(File original) {
        Set<File> set = getFolderContents(original);

        for (File myFile: set) {
            File newFile = createFile(original.getAbsolutePath(), myFile.getName());
            if (!myFile.renameTo(newFile)) {
                log.error("unable to rename file: {}", myFile);
            }
        }

        for (File theFile: Objects.requireNonNull(original.listFiles())) {
            if (theFile.isDirectory()) {
                deleteDirectory(theFile);
            }
        }
    }

    private static Set<File> getFolderContents(File folder) {
        Set<File> set = new HashSet<>();

        for (File file: Objects.requireNonNull(folder.listFiles())) {
            if (file.isDirectory()) {
                set.addAll(getFolderContents(file));
            } else if (file.isFile()) {
                if (!file.getName().contains(StringConstants.DS_Store)) {
                    set.add(file);
                }
            } else {
                log.error("unknown file: {}", file);
            }
        }

        return set;
    }

    private static void deleteDirectory(File path) {
        if (path.exists()) {
            for (File theFile: Objects.requireNonNull(path.listFiles())) {
                if (theFile.isDirectory()) {
                    deleteDirectory(theFile);
                } else {
                    deleteFile(theFile);
                }
            }
        }
        deleteFile(path);
    }

    private static void deleteFile(File theFile) {
        if (!theFile.delete()) {
            log.error("unable to delete file: {}", theFile);
        }
    }

}
