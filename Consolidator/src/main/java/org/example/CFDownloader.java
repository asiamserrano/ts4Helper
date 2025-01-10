//package org.example;
//
//import java.io.File;
//import java.io.InputStream;
//import java.net.HttpURLConnection;
//import java.net.URL;
//import java.net.URLConnection;
//import java.nio.charset.StandardCharsets;
//import java.util.*;
//
//import static org.example.Constants.USER_AGENT_KEY;
//import static org.example.Constants.USER_AGENT_VALUE;
//import static org.example.Utilities.*;
//
//public class CFDownloader {
//
//    // https://www.curseforge.com/api/v1/mods/1008775/files/6029686/download
//    // https://www.curseforge.com/sims4/create-a-sim/goldfish-spring-breath-dress
//
//    public static void main(String[] args) throws Exception {
//
//        OkHttpClient client = new OkHttpClient().newBuilder()
//                .build();
//        MediaType mediaType = MediaType.parse("text/plain");
//        RequestBody body = RequestBody.create(mediaType, "");
//        Request request = new Request.Builder()
//                .url("https://www.patreon.com/posts/faine-platforms-63063259")
//                .method("GET", body)
//                .build();
//        Response response = client.newCall(request).execute();
//
////        Map<String, String> map = new HashMap<>() {{
////            put("User-Agent", "PostmanRuntime/7.43.0");
////            put("Accept", "*/*");
////            put("Cache-Control", "no-cache");
////            put("Host", "www.patreon.com");
////            put("Accept-Encoding", "gzip, deflate, br");
////            put("Connection", "keep-alive");
////        }};
////
////        URL url = new URL("https://www.patreon.com/posts/faine-platforms-63063259");
////        URLConnection openConnection = url.openConnection();
////        for (String key: map.keySet()) openConnection.addRequestProperty(key, map.get(key));
////        openConnection.getInputStream();
//
//    }
//
////    public static void main(String[] args) throws Exception {
////        URL url = new URL("https://www.patreon.com/posts/faine-platforms-63063259");
//////        URL url = new URL("https://www.simsfinds.com/downloads/151949/helgatisha-recolor-ep03-romper-sims4");
//////        URL url = new URL("https://www.curseforge.com/sims4/create-a-sim/goldfish-spring-breath-dress");
////        String userCredentials = "username:password";
////        String basicAuth = "Basic " + new String(Base64.getEncoder().encode(userCredentials.getBytes()));
////
////
////        URLConnection openConnection = url.openConnection();
////        String encoding3 = Base64.getEncoder().encodeToString(":scheme".getBytes(StandardCharsets.UTF_8));
////        openConnection.addRequestProperty(encoding3, "https");
//////        openConnection.addRequestProperty("User-Agent", "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_15_7) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/131.0.0.0 Safari/537.36");
//////        openConnection.addRequestProperty("Cookie", "a_csrf=7VgXF02hPLycZg65wxWbhE-g3ZF7hMaLzVF9QYOteLI; patreon_locale_code=en-US; patreon_location_country_code=US; patreon_device_id=73d4df1d-b5cb-49c7-9a34-8b30cbe29644; __ssid=c2c3dda92723a89077ab95c1dff74bf; analytics_session_id=bca48a4b-aa6e-4993-9eaa-ad39383e1cb0; __cf_bm=iY.9Xfpvk2Vw9_6Ip5B_TWKBX93mfNJe745p2BNE.8A-1736465033-1.0.1.1-Ij_fV8rIPGLpGAHxqKd6KCHfiPDoUfRUjUixTRf6f42X4XI6GiYpl_EYX9NO2CbsgrF9GIjXX4A_2UbTf9GxCsqO6nRJ9DjXK477eZLzxaA; a_csrf=7VgXF02hPLycZg65wxWbhE-g3ZF7hMaLzVF9QYOteLI");
//////        openConnection.addRequestProperty("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,image/avif,image/webp,image/apng,*/*;q=0.8,application/signed-exchange;v=b3;q=0.7");
//////        openConnection.addRequestProperty("Accept-Encoding", "gzip, deflate, br, zstd");
//////        openConnection.addRequestProperty("Accept-Language", "en-US,en;q=0.9");
//////        openConnection.addRequestProperty("Cache-Control", "max-age=0");
//////        openConnection.addRequestProperty("Priority", "u=0, i");
//////        openConnection.addRequestProperty("Sec-Ch-Ua", "\"Google Chrome\";v=\"131\", \"Chromium\";v=\"131\", \"Not_A Brand\";v=\"24\"");
//////        openConnection.addRequestProperty("xxx", "xxx");
//////        Map<String, List<String>> map = openConnection.getRequestProperties();
////
//////        for (String s: map.keySet()) {
//////            System.out.println(s);
//////            for (String value: map.get(s)) System.out.println(value);
//////            System.out.println("______");
//////        }
////        File file = getFile("input.txt");
////        List<String> content = getFileContentList(file);
////
////        int index, size = content.size() / 2;
//////
////        String key, value;
////        for (int i = 0; i < size; i++) {
////            index = (i * 2) + 1;
////            key = content.get(index - 1);
////            value = content.get(index);
////            if (!key.startsWith(":")) {
////                key = getRequestPropertyKey(key);
////                openConnection.addRequestProperty(key, value);
////            }
////        }
////
////        openConnection.getInputStream();
////
//////        openConnection.connect();
////
////
////    }
//
//    private static String getRequestPropertyKey(String string) {
//        String[] strings = string.replace(":", "").split("-");
//        List<String> list = new ArrayList<>();
//        for (String str: strings) list.add(str.substring(0, 1).toUpperCase() + str.substring(1));
//        return String.join("-", list);
//    }
//
//}
