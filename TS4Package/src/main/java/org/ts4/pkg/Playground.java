package org.example.ts4package;

import lombok.extern.slf4j.Slf4j;
import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;
import okhttp3.Response;
import okhttp3.internal.http2.Header;
import org.apache.commons.io.FileUtils;
import org.example.ts4package.utilities.OkHttpUtility;
import org.example.ts4package.utilities.StringUtility;
import org.example.ts4package.utilities.URLUtility;

import java.io.*;
import java.net.URI;
import java.net.URL;
import java.net.http.HttpClient;
import java.net.http.HttpHeaders;
import java.net.http.HttpRequest;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.time.Duration;
import java.util.*;

@Slf4j
class Playground {

    public static final String SPACE = " ";
    public static final String UNDERSCORE = "_";
    public static final String DASH = "-";

    public static final OkHttpClient okHttpClient = new OkHttpClient();

    public static int count = 0;

    public static void main(String[] args) throws Exception {
        List<String> strings = StringUtility.loadResourceList("input.txt");
        for (String s : strings) {
            if (s.contains("/download/")) {

                //https://cdn.simfileshare.net/download/2835904/?dl
                String link = StringUtility.getStringBetweenRegex(s, "<a href=\"", "/\">");
                link = String.format("https://cdn.simfileshare.net%s/?dl", link);
                String fileName = StringUtility.getStringBetweenRegex(s, ">", "<");
                download(link, fileName);
            }
        }
    }

    private static void download(String link, String fileName) throws Exception {
        count++;
        if (count <= 5) {
            URL url = URLUtility.createURLNoException(link);
            File file = new File("/Users/asia/ChromeDownloads/", fileName);
            if (file.exists()) file.delete();
            file.createNewFile();
            Response response = OkHttpUtility.sendRequest(url, okHttpClient);
            InputStream in = response.body().byteStream();
            Files.copy(in, Paths.get(file.toURI()), StandardCopyOption.REPLACE_EXISTING);
            response.close();
        }
    }

//    public static final Map<String, Integer> map = new HashMap<>();
//    public static String KEY = "";
//    public static Integer VALUE = 0;
//
//    public static void main(String[] args) {
//
//        File file = new File("/Volumes/TS4SSD/The Sims 4/Mods/cc/CAS");
//        listFiles(file).stream().sorted(Comparator.comparing(File::getName)).forEach(f -> {
//            String fileName = f.getName().toLowerCase();
//            if (fileName.contains("af".toLowerCase()) &&  !fileName.contains("] ")) {
//                fileName = fileName
//                        .strip()
//                        .replaceAll(" - ", UNDERSCORE)
//                        .replaceAll(SPACE, UNDERSCORE)
//                        .replaceAll(DASH, UNDERSCORE)
//                        .replaceAll("__", UNDERSCORE)
////                        .replaceAll("trillqueen", "[trillqueen] ")
//                        ;
//
//                System.out.println(fileName);
////                renameFile(f, fileName);
//
////                if (fileName.contains(UNDERSCORE)) {
////                    String k = fileName.split(UNDERSCORE)[0];
////                    int v = map.getOrDefault(k, 0) + 1;
////                    map.put(k, v);
////                    if (v > VALUE) {
////                        VALUE = v;
////                        KEY = k;
////                    }
////                }
//            }
//        });
//
//        if (!KEY.isEmpty() && VALUE > 0) {
//            System.out.println("largest key: " + KEY);
//        }
//    }

    private static List<File> listFiles(File file) {
        return listFiles(file, new ArrayList<>());
    }

    private static List<File> listFiles(File file, List<File> files) {
        for (File f: Objects.requireNonNull(file.listFiles())) {
            if (f.isDirectory()) {
                files.addAll(listFiles(f));
            } else {
                if (!f.getName().equals(".DS_Store")) files.add(f);
            }
        }
        return files;
    }

    private static void renameFile(File f, String fn) {
        String old = f.getName();
        File nf = new File(f.getParent(), fn);
        if (f.renameTo(nf)) {
            String format = String.format("renamed %-50s to %s", old, fn);
            log.info(format);
        } else {
            log.error("unable to rename file: {}", old);
        }
    }

}
