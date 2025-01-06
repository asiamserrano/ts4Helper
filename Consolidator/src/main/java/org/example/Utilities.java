package org.example;

import java.io.File;

import static org.example.Constants.ERROR_MESSAGE_FORMAT;
import static org.example.Constants.UNABLE_TO_DELETE;

public class Utilities {

    public static void deleteFile(File theFile) {
        if (!theFile.delete()) {
            printErrorMessage(UNABLE_TO_DELETE, theFile);
        }
    }

    public static void printErrorMessage(String message, File theFile) {
        if (!theFile.delete()) {
            String error = format(ERROR_MESSAGE_FORMAT, message, theFile.getAbsolutePath());
            System.out.println(error);
        }
    }

    private static String format(String format, String a, String b) {
        return String.format(format, a, b);
    }

}
