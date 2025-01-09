package org.example;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLConnection;
import java.net.http.HttpRequest;
import java.nio.channels.Channels;
import java.nio.channels.FileChannel;
import java.nio.channels.ReadableByteChannel;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class SFDownloader {

    public static String EMPTY = "";
    public static String SINGLE_QUOTE = "\"";
    public static String COMMA = ",";
    public static String PATREON_LINK = "https://www.patreon.com/posts/";
    public static String DOWNLOAD_LOCATION = "/Users/asiaserrano/ChromeDownloads/";

//    public static void main(String[] args) throws Exception {
//        File file = getFile("temp.html");
//        String content = getContent(file);
//        downloadPatreon(content);
//    }

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
                        String download_content = getContent(download_url);

                        String key = regex(download_content, "key=", "\"");
                        String continue_url_path = "https://www.simsfinds.com/continue?key=" + key;
                        URL continue_url = new URL(continue_url_path);
                        String continue_content = getContent(continue_url);

                        String[] info = regex(continue_content, "data-at5t768r9=\"", SINGLE_QUOTE).split(COMMA);
                        String flid = regex(continue_content, "data-at8r136r7=\"", SINGLE_QUOTE);
                        String pass = regex(continue_content, "data-passe=\"", SINGLE_QUOTE);

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
                            String sf_content = getContent(sf_url);
                            generated = regex(sf_content, "<title>", "</title");

                            if (generated.contains("ruchellsims-cc.blogspot.com")) {
                                URL ruchellsims_url = new URL(generated);
                                String pateron_link = regex(getContent(ruchellsims_url), PATREON_LINK, SINGLE_QUOTE);
                                generated = PATREON_LINK + pateron_link;
                            }

                        }

                        if (generated.contains("https://click.simsfinds.com/download?")) {
                            info = line.split("/");
                            line = info[info.length - 1];
                            downloadSF(generated, line);
                        }
//                        else if (generated.contains("https://www.patreon.com/posts/")) {
//                            URL patreon = new URL(generated);
//                            downloadPatreon(patreon);
//                        }
                        else {
//                            System.out.println("saving: " + generated);
                            writer.write(generated);
                            writer.newLine(); // Add a new line
                        }

//                        if (!generated.contains("https://www.patreon.com/posts/") && !generated.contains("https://click.simsfinds.com/download?")) {
//                            System.out.println(generated);
//                        }
//
//                        writer.write(generated);
//                        writer.newLine(); // Add a new line
                    }
                }
            } catch (IOException e) {
                System.err.println("An error occurred: " + e.getMessage());
            }
        }
    }

//    public static void downloadPatreon(URL patreon_file) throws Exception {
//        System.out.println("downloading patreon link: " + patreon_file);
//        String content = getContent(patreon_file);
//        String folder_name = content.split("elementtiming=\"post-title\"")[1]
//                .split(">")[1].split("<")[0];
//        File folder = new File(folder_name);
//
//        if (createFolder(folder)) {
//            downloadPatreon(content, folder);
//        }
//    }

    private static boolean createFolder(File folder) {
        String folder_name = folder.getAbsolutePath();
        if (!folder.exists()) {
            if (folder.mkdir()) {
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

//    private static void downloadPatreon(String content, File location) throws Exception {
//        List<String> filenames = Arrays.stream(Objects.requireNonNull(location.listFiles())).
//                map(File::getName)
//                .toList();
//        Pattern pattern = Pattern.compile("\"download_url\":.*?,\"image_urls\"");
//        Matcher matcher = pattern.matcher(content);
//        String match, file_name, download_url, file_location;
//        while (matcher.find()) {
//            match = matcher.group();
//            if (!match.contains("1.png")) {
//                file_name = match.split("\"file_name\":\"")[1].split("\",\"")[0];
//                download_url = match.split("\",")[0].replace("\"download_url\":\"", EMPTY).replace("\\u0026token", "&token");
//                if (!filenames.contains(file_name)) {
//                    file_location = String.format("%s/%s", location.getAbsolutePath(), file_name);
//                    download(download_url, file_location);
//                } else {
//                    System.out.println("file already copied: " + file_name );
//                }
//            }
//        }
//    }

    private static void downloadSF(String url, String name) throws Exception {
        System.out.println("downloading simsfinds link: " + url);
        String folder_name = String.format("%s%s", DOWNLOAD_LOCATION, name);
        String file_name = String.format("%s.package", name);
        File folder = new File(folder_name);
        if (createFolder(folder)) {
            String string = String.format("%s/%s", folder_name, file_name);
            download(url, string);
        }
    }

    private static void download(String download_url, String file_location) throws Exception {
        URL url = new URL(download_url);
        ReadableByteChannel readableByteChannel = Channels.newChannel(url.openStream());
        FileOutputStream fileOutputStream = new FileOutputStream(file_location);
        FileChannel fileChannel = fileOutputStream.getChannel();
        fileChannel.transferFrom(readableByteChannel, 0, Long.MAX_VALUE);
    }

//        public static void main(String[] args) throws Exception {
//        String sf_link = "https://click.simsfinds.com/download?pass=2307102333&version=1673510530&key=c145bdd78f6446d18375aec5b0daaf39&cid=318454";
////        String url_link = "https://click.simsfinds.com/download?pass=2307102333&version=1671882284&key=3ac69fc3916bdaffb78c0747fa717047&cid=317571";
//        URL sf_url = new URL(sf_link);
//        String sf_content = getContent(sf_url);
//        String ruchellsims_link = regex(sf_content, "<title>", "</title");
//
//
////        System.out.println(item);
////        Pattern pattern = Pattern.compile("https://www.patreon.com/posts/.*?\"");
//
////        for (String line : content) System.out.println(line);
//
////        try {
////            File file = getFile("input.txt");
////            Scanner scanner = new Scanner(file);
////            while (scanner.hasNextLine()) {
////                URL url = new URL(scanner.nextLine());
////                String content = getContents(url);
////                String generated = generateSFURL(content);
////                System.out.println(generated);
////            }
////        } catch (Exception e){
////            e.printStackTrace();
////        }
//    }

    /*
 step 1      change bookmarks to -> https://www.simsfinds.com/continue?key=<key>
 step 2      check content for 'data-at8r136r7' value
                 == 0 -> get external download link (patreon, overkillsimmer, etc...)
                 > 0  -> is direct download link

  */

    public static String getContent(URL url) throws Exception {
        URLConnection openConnection = url.openConnection();
        openConnection.addRequestProperty("User-Agent", "Mozilla/5.0 (Windows NT 6.1; WOW64; rv:25.0) Gecko/20100101 Firefox/25.0");
//        String redirect = openConnection.getHeaderField("Location");
//        if (redirect != null){
//            openConnection = new URL(redirect).openConnection();
//            openConnection.addRequestProperty("User-Agent", "Mozilla/5.0 (Windows NT 6.1; WOW64; rv:25.0) Gecko/20100101 Firefox/25.0");
//        }
        BufferedReader r = new BufferedReader(new InputStreamReader(openConnection.getInputStream()));
        return r.lines().collect(Collectors.joining(EMPTY));
    }

    public static List<String> getContents(URL url) throws Exception {
        URLConnection openConnection = url.openConnection();
        openConnection.addRequestProperty("User-Agent", "Mozilla/5.0 (Windows NT 6.1; WOW64; rv:25.0) Gecko/20100101 Firefox/25.0");
        String redirect = openConnection.getHeaderField("Location");
        if (redirect != null){
            openConnection = new URL(redirect).openConnection();
            openConnection.addRequestProperty("User-Agent", "Mozilla/5.0 (Windows NT 6.1; WOW64; rv:25.0) Gecko/20100101 Firefox/25.0");
        }
        BufferedReader r = new BufferedReader(new InputStreamReader(openConnection.getInputStream()));
        return r.lines().collect(Collectors.toList());
    }

    public static String regex(String matcher, String p1, String p2) {
        String s = String.format("%s.*?%s", p1, p2);
        Pattern p = Pattern.compile(s);
        Matcher m = p.matcher(matcher);

        if (m.find()) {
            return m.group().replace(p1, EMPTY).replace(p2, EMPTY).replace(SINGLE_QUOTE, EMPTY);
        } else {
            return EMPTY;
        }
    }

    public static File getFile(String file) {
        URL resource = SFDownloader.class.getClassLoader().getResource(file);
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

    public static String getContent(File file) throws Exception {
        List<String> result = Files.readAllLines(Paths.get(file.getAbsolutePath()));
        return String.join(EMPTY, result);
    }

}
