package ts4.helper.TS4Downloader;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;
import ts4.helper.TS4Downloader.utilities.OkHttpUtility;
import ts4.helper.TS4Downloader.utilities.StringUtility;
import ts4.helper.TS4Downloader.utilities.URLUtility;

import java.net.URL;
import java.util.*;
import java.util.regex.Matcher;
import java.util.stream.Collectors;

import static ts4.helper.TS4Downloader.constants.StringConstants.*;
import static ts4.helper.TS4Downloader.constants.OkHttpConstants.HTTPS_SCHEME;
//import static ts4.helper.TS4Downloader.enums.WebsiteEnum.CURSE_FORGE_CAS;
import static ts4.helper.TS4Downloader.enums.DomainEnum.CURSE_FORGE;

@Slf4j
public class Main {

    public static void main(String[] args) throws Exception {
        String content = StringUtility.loadResource("html_file.html");
//        String url_str = "https://alenaivanisova.my.curseforge.com/?projectsPage=14&projectsSearch=&projectsSort=9";
//        URL url = URLUtility.createURL(url_str);

//        Set<String> lines = StringUtility.getSetBetweenRegex(content, "href=\"/sims4/create-a-sim/", "/download/");
//

//        Set<String> set = StringUtility.getSetBetweenRegex(content, "\"downloadLink\":\"", SINGLE_QUOTE);
//        List<String> lines = set.stream().map(s -> s.replaceAll("\\\\", EMPTY)).toList();

        List<String> links = new ArrayList<>(StringUtility.getSetBetweenRegex(content, "\"downloadLink\":\"", SINGLE_QUOTE))
                .stream()
                .map(s -> s.replaceAll("\\\\", EMPTY))
                .toList();

        for (String line : links) System.out.println(line);



//
       //  https://edge.forgecdn.net/files/6068/782/GoldFish%20Cross%20Top.zip


//        for (String link : links) System.out.println(link);

//        String page_string = StringUtility.getStringBetweenRegex(url.toString(), "page=", "&");
//        int page = Integer.parseInt(page_string);
//
//        String content = OkHttpUtility.getContent(url, new OkHttpClient());
//        List<String> links = new ArrayList<>(StringUtility.getSetBetweenRegex(content, "btn-cta\" href=\"/", "\">").stream()
//                .filter(str -> str.contains("/install"))
//                .map(str -> str.replace("/install", ""))
//                .map(str -> String.format("https://www.%s/%s", CURSE_FORGE.name, str))
//                .toList());


//        String content = OkHttpUtility.getContent(url, new OkHttpClient());
//        String download_url = "https://www.curseforge.com/api/v1/mods/%s/files/%s/download";
//        String id1 = StringUtility.getStringBetweenRegex(content, "\"identifier\":\"", SINGLE_QUOTE);
//        String id2 = StringUtility.getStringBetweenRegex(content, "\"mainFile\":{\"id\":", COMMA);
//        String source_url = String.format(download_url, id1, id2);
//        URL newURL = URLUtility.createURL(source_url);
//        System.out.println(newURL.toString());

//        List<> = getURL(Coll);

//        String page_string = StringUtility.getStringBetweenRegex(url.toString(), "page=", "&");
//        int page = Integer.parseInt(page_string);
//
////        List<String> links = StringUtility.getSetBetweenRegex(content, "btn-cta\" href=\"/", "\">").stream()
////                .filter(str -> str.contains("download"))
////                .map(str -> String.format("https://www.%s/%s", CURSE_FORGE.name, str))
////                .toList();
//
//        List<HttpUrl> links = StringUtility.getSetBetweenRegex(content, "btn-cta\" href=\"/", "\">").stream()
//                .filter(str -> str.contains("download"))
//                .map(str -> new HttpUrl.Builder()
//                        .scheme(HTTPS_SCHEME)
//                        .host(CURSE_FORGE.name)
//                        .addPathSegments(str)
//                        .build()
//                )
////                .map(str -> String.format("https://www.%s/%s", CURSE_FORGE.name, str))
//                .toList();
//
//        for (HttpUrl link : links) System.out.println(link.toString());
//        List<URL> urls = getURLs(links);
//        for(URL url1:urls) System.out.println(url1.toString());
    }

//    private static List<String> getLinks(URL url, List<String> list) throws Exception {
//        String page_string = StringUtility.getStringBetweenRegex(url.toString(), "page=", "&");
//        int next, page = Integer.parseInt(page_string);
//        log.info("searching page {}", page);
//        String content = OkHttpUtility.getContent(url, new OkHttpClient());
//        List<String> links = new ArrayList<>(StringUtility.getSetBetweenRegex(content, "btn-cta\" href=\"/", "\">").stream()
//                .filter(str -> str.contains("/install"))
//                .map(str -> str.replace("/install", ""))
//                .map(str -> String.format("https://www.%s/%s", CURSE_FORGE.name, str))
//                .toList());
//        if (links.isEmpty()) {
//            if (content.contains("Just a moment...")) {
//                log.info("retrying {}", url);
//                Thread.sleep(2000);
//                return getLinks(url, list);
//            } else {
//                return list;
//            }
//        } else {
//            next = page + 1;
//            for (String link : links) log.info(link);
//            list.addAll(links);
//
//            String urlString = url.toString().replace("page=" + page, "page=" + next);
//            URL newUrl = URLUtility.createURL(urlString);
//            return getLinks(newUrl, list);
//        }
//    }

//    private static List<URL> getURLs(List<String> list) {
//        try {
//            List<URL> urls = new ArrayList<>();
//            for (String s: list) urls.add(URLUtility.createURL(s));
//            return urls;
//        } catch (Exception e) {
//            return new ArrayList<>();
//        }
//    }

}

@Getter
@Setter
@AllArgsConstructor
class SimsFindsParameters {

    @JsonProperty("flid")
    private String flid;

    @JsonProperty("cid")
    private String cid;

    @JsonProperty("key")
    private String key;

    @JsonProperty("version")
    private String version;

    @JsonProperty("pass")
    private String pass;


}