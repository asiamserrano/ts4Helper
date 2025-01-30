package org.example.utilities;

import lombok.extern.slf4j.Slf4j;

import java.io.File;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

import static org.example.ts4package.constants.StringConstants.DS_Store;

@Slf4j
public abstract class ConsolidateUtility {

    public static void consolidate(File original) {
        Set<File> set = getFolderContents(original);

        for (File myFile: set) {
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
