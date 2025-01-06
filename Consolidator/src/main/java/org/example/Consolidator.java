package org.example;

import java.io.File;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

import static org.example.Constants.*;
import static org.example.Utilities.deleteFile;
import static org.example.Utilities.printErrorMessage;

public class Consolidator {

    public static void main(String[] args)  {
        String STARTING_FOLDER = args[0];

        if (STARTING_FOLDER != null) {
            consolidate(STARTING_FOLDER);
        } else {
            System.out.println("no starting folder provided");
        }
    }

    public static void consolidate(String path) {
        File original = new File(path);
        Set<File> set = getFolderContents(original);

        for (File myFile: set) {
            String newPath = String.format(PATH_JOINER_FORMAT, original.getAbsolutePath(), myFile.getName());
            File newFile = new File(newPath);
            if (!myFile.renameTo(newFile)) {
                printErrorMessage(UNABLE_TO_MOVE, newFile);
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
                printErrorMessage(UNKNOWN_OBJECT_ERROR, file);
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

}
