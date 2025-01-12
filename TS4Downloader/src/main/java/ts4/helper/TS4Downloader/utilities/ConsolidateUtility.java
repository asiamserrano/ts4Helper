package ts4.helper.TS4Downloader.utilities;

import lombok.extern.slf4j.Slf4j;

import java.io.File;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

import static ts4.helper.TS4Downloader.constants.StringConstants.DS_Store;

@Slf4j
public abstract class ConsolidateUtility {

    public static void consolidate(File original) {
//        File original = new File(path);
        Set<File> set = getFolderContents(original);

        for (File myFile: set) {
//            String newPath = String.format(PATH_JOINER_FORMAT, original.getAbsolutePath(), myFile.getName());

            File newFile = new File(original, myFile.getName());
            if (!myFile.renameTo(newFile)) {
                log.error("unable to move object: {}", newFile);
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
                if (!file.getName().contains(DS_Store)) {
                    set.add(file);
                }
            } else {
                log.error("unknown object: {}", file);
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
                    FileUtility.deleteFile(theFile);
                }
            }
        }
        FileUtility.deleteFile(path);
    }

}
