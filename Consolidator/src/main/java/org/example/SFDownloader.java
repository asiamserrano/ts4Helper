package org.example;

import java.io.*;
import java.net.URL;
import java.net.URLConnection;
import java.nio.channels.Channels;
import java.nio.channels.FileChannel;
import java.nio.channels.ReadableByteChannel;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import static org.example.Constants.EMPTY;
import static org.example.Constants.SINGLE_QUOTE;
import static org.example.Utilities.*;

public class SFDownloader {

    public static String COMMA = ",";
    public static String PATREON_LINK = "https://www.patreon.com/posts/";
    public static String DOWNLOAD_LOCATION = "/Users/asiaserrano/ChromeDownloads/";

    public static void main(String[] args) throws Exception {

        File bookmarks = getFile("bookmarks_1_8_25.html");

        if (bookmarks == null) {
            System.out.println("cannot load file: " + bookmarks);
        } else {
            Scanner scanner = new Scanner(bookmarks);
            String line;

            try (BufferedWriter writer = new BufferedWriter(new FileWriter("output.txt"))) {

                while (scanner.hasNextLine()) {
                    line = scanner.nextLine();
                    if (line.contains("https://www.simsfinds.com/downloads/")) {
                        line = line.split("\"")[1];
                        URL download_url = new URL(line);
                        String download_content = getURLContentString(download_url);

                        String key = regexBetweenStrings(download_content, "key=", "\"");
                        String continue_url_path = "https://www.simsfinds.com/continue?key=" + key;
                        URL continue_url = new URL(continue_url_path);
                        String continue_content = getURLContentString(continue_url);

                        String[] info = regexBetweenStrings(continue_content, "data-at5t768r9=\"", SINGLE_QUOTE).split(COMMA);
                        String flid = regexBetweenStrings(continue_content, "data-at8r136r7=\"", SINGLE_QUOTE);
                        String pass = regexBetweenStrings(continue_content, "data-passe=\"", SINGLE_QUOTE);

                        Map<String, String> map = new HashMap<>();
                        map.put("cid", info[0]);
                        map.put("key", key);
                        map.put("version", info[3]);
                        map.put("pass", pass);
                        map.put("flid", flid);

                        List<String> list = map.entrySet().stream().
                                map(e -> String.format("%s=%s", e.getKey(), e.getValue()))
                                .collect(Collectors.toList());

                        String generated = String.format("https://click.simsfinds.com/download?%s", String.join("&", list));

                        if (flid.equals("0")) {
                            URL sf_url = new URL(generated);
                            String sf_content = getURLContentString(sf_url);
                            generated = regexBetweenStrings(sf_content, "<title>", "</title");

                            if (generated.contains("ruchellsims-cc.blogspot.com")) {
                                URL ruchellsims_url = new URL(generated);
                                String pateron_link = regexBetweenStrings(getURLContentString(ruchellsims_url), PATREON_LINK, SINGLE_QUOTE);
                                generated = PATREON_LINK + pateron_link;
                            }
                        }

                        if (generated.contains("https://click.simsfinds.com/download?")) {
                            info = line.split("/");
                            line = info[info.length - 1];
                            downloadSF(generated, line);
                        } else if (generated.contains("https://www.patreon.com/posts/")) {
                            URL patreon = new URL(generated);
                            downloadPatreon(patreon);
                        } else {
                            System.out.println("saving: " + generated);
                            writer.write(generated);
                            writer.newLine(); // Add a new line
                        }
                    }
                }
            } catch (IOException e) {
                System.err.println("An error occurred: " + e.getMessage());
            }
        }
    }

    private static void downloadPatreon(URL patreon_file) throws Exception {
        System.out.println("downloading patreon link: " + patreon_file);
        String content = getURLContentString(patreon_file);
        String folder_name = content.split("elementtiming=\"post-title\"")[1]
                .split(">")[1].split("<")[0];
        File folder = new File(folder_name);

        if (createDirectory(folder)) {
            downloadPatreon(content, folder);
        }
    }

    private static void downloadPatreon(String content, File location) throws Exception {
        List<String> filenames = Arrays.stream(Objects.requireNonNull(location.listFiles())).
                map(File::getName)
                .toList();
        Pattern pattern = Pattern.compile("\"download_url\":.*?,\"image_urls\"");
        Matcher matcher = pattern.matcher(content);
        String match, file_name, download_url, file_location;
        while (matcher.find()) {
            match = matcher.group();
            if (!match.contains("1.png")) {
                file_name = match.split("\"file_name\":\"")[1].split("\",\"")[0];
                download_url = match.split("\",")[0].replace("\"download_url\":\"", EMPTY).replace("\\u0026token", "&token");
                if (!filenames.contains(file_name)) {
                    file_location = String.format("%s/%s", location.getAbsolutePath(), file_name);
                    download(download_url, file_location);
                } else {
                    System.out.println("file already copied: " + file_name );
                }
            }
        }
    }

    private static void downloadSF(String url, String name) throws Exception {
        System.out.println("downloading simsfinds link: " + url);
        String folder_name = String.format("%s%s", DOWNLOAD_LOCATION, name);
        String file_name = String.format("%s.package", name);
        File folder = new File(folder_name);
        if (createDirectory(folder)) {
            String string = String.format("%s/%s", folder_name, file_name);
            download(url, string);
        }
    }

    private static void download(String download_url, String file_location) throws Exception {
        URL url = new URL(download_url);
        Utilities.download(url, file_location);
    }

}
