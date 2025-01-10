package org.example;

import java.io.*;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLConnection;
import java.nio.channels.Channels;
import java.nio.channels.FileChannel;
import java.nio.channels.ReadableByteChannel;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.example.Constants.*;

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

    public static File getFile(String file) {
        URL resource = Utilities.class.getClassLoader().getResource(file);
        if (resource == null) {
            throw new IllegalArgumentException("file not found!");
        } else {
            try {
                return new File(resource.toURI());
            } catch (URISyntaxException | IllegalArgumentException e) {
                return null;
            }
        }
    }

    public static List<String> getFileContentList(File file) throws IOException {
        return Files.readAllLines(Paths.get(file.getAbsolutePath()));
    }

    public static String getFileContentString(File file) throws IOException {
        List<String> result = getFileContentList(file);
        return String.join(EMPTY, result);
    }

    public static List<String> getURLContentList(URL url) throws IOException {
        return getURLContent(url).collect(Collectors.toList());
    }

    public static String getURLContentString(URL url) throws Exception {
        return getURLContent(url).collect(Collectors.joining(EMPTY));
    }

    public static boolean createDirectory(File directory) {
        String folder_name = directory.getAbsolutePath();
        if (!directory.exists()) {
            if (directory.mkdir()) {
                System.out.println("folder created for " + folder_name);
                return true;
            } else {
                System.out.println("folder cannot be created for" + folder_name);
                return false;
            }
        } else {
            System.out.println("existing folder for " + folder_name);
            return true;
        }
    }

    public static void download(URL url, String file_location) throws Exception {
        ReadableByteChannel readableByteChannel = Channels.newChannel(url.openStream());
        FileOutputStream fileOutputStream = new FileOutputStream(file_location);
        FileChannel fileChannel = fileOutputStream.getChannel();
        fileChannel.transferFrom(readableByteChannel, 0, Long.MAX_VALUE);
    }

    public static String regexBetweenStrings(String content, String p1, String p2) {
        String s = String.format("%s.*?%s", p1, p2);
        Pattern p = Pattern.compile(s);
        Matcher m = p.matcher(content);

        if (m.find()) {
            return m.group().replace(p1, EMPTY).replace(p2, EMPTY).replace(SINGLE_QUOTE, EMPTY);
        } else {
            return EMPTY;
        }
    }

    private static Stream<String> getURLContent(URL url) throws IOException {
        URLConnection openConnection = getURLConnection(url);
        String redirect = openConnection.getHeaderField("Location");
        if (redirect != null){
            openConnection = getURLConnection(new URL(redirect));
        }
        BufferedReader r = new BufferedReader(new InputStreamReader(openConnection.getInputStream()));
        return r.lines();
    }

    private static URLConnection getURLConnection(URL url) throws IOException {
        URLConnection openConnection = url.openConnection();
        openConnection.addRequestProperty(USER_AGENT_KEY, USER_AGENT_VALUE);
        return openConnection;
    }

}
