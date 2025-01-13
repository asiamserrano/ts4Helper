package ts4.helper.TS4Downloader.controllers;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import okhttp3.Cookie;
import okhttp3.CookieJar;
import okhttp3.OkHttpClient;
import okhttp3.Response;
import org.springframework.web.bind.annotation.*;
import ts4.helper.TS4Downloader.enums.ResponseEnum;
import ts4.helper.TS4Downloader.models.DownloadResponse;
import ts4.helper.TS4Downloader.utilities.*;

import java.io.File;
import java.net.URL;
import java.util.*;
import java.util.regex.Matcher;

import static ts4.helper.TS4Downloader.constants.StringConstants.*;
import static ts4.helper.TS4Downloader.enums.ResponseEnum.SUCCESSFUL;
import static ts4.helper.TS4Downloader.enums.ResponseEnum.FAILURE;
import static ts4.helper.TS4Downloader.enums.ResponseEnum.UNKNOWN;

import static ts4.helper.TS4Downloader.constants.ControllerConstants.CURSE_FORGE_CONTROLLER_REQUEST_MAPPING;
import static ts4.helper.TS4Downloader.constants.ControllerConstants.CURSE_FORGE_CONTROLLER_COOKIE_STATUS_GET_MAPPING;
import static ts4.helper.TS4Downloader.constants.ControllerConstants.CURSE_FORGE_CONTROLLER_UPDATE_COOKIE_POST_MAPPING;

import static ts4.helper.TS4Downloader.enums.WebsiteEnum.CURSE_FORGE;

@RestController
@RequestMapping(CURSE_FORGE_CONTROLLER_REQUEST_MAPPING)
@Slf4j
@AllArgsConstructor
public class CurseForgeController {

    private OkHttpClient client;
    private CookieJar cookieJar;

    @GetMapping(CURSE_FORGE_CONTROLLER_COOKIE_STATUS_GET_MAPPING)
    public ResponseEnum cookieStatus() {
        ResponseEnum responseEnum;
        try {
            Response response = OkHttpUtility.sendRequest(CURSE_FORGE.httpUrl, client);
            boolean bool = response.isSuccessful();
            response.close();
            responseEnum = bool ? SUCCESSFUL : FAILURE;
            log.info("curse forge cookie is {}", bool ? "active" : "inactive");
        } catch (Exception e) {
            log.error("cannot check status of curse forge cookie. Exception: {} ", e.getMessage());
            responseEnum = UNKNOWN;
        }
        return responseEnum;
    }

    @PostMapping(CURSE_FORGE_CONTROLLER_UPDATE_COOKIE_POST_MAPPING)
    public ResponseEnum updateCookie(@RequestBody String body) {
        String cookie_body = body.strip();
        Cookie cookie = OkHttpUtility.createCookie(cookie_body, CURSE_FORGE);
        List<Cookie> cookies = Collections.singletonList(cookie);
        cookieJar.saveFromResponse(CURSE_FORGE.httpUrl, cookies);
        log.info("cookie for {} set to {}", CURSE_FORGE.url, cookie_body);
        return cookieStatus();
    }

//    @GetMapping("/searchResults")
//    public String searchResults(@RequestBody String searchURL) {
//        try {
//
////            String url = String.format("https://www.curseforge.com/members/%s/projects?", member);
////            String parameters = "page=%d&pageSize=20&sortBy=ReleaseDate&sortOrder=Desc";
//
////            URL url = URLUtility.createURL(searchURL);
////            log.info("curse forge search url: {}", url);
////            String content = OkHttpUtility.getContent(url, client);
////            String prefix = "<a href=\"/sims4/create-a-sim/";
////            Matcher matcher = StringUtility.getRegexBetweenMatcher(content, prefix, SINGLE_QUOTE);
////            Set<String> set = new HashSet<>();
////            while (matcher.find()) {
////                String match = matcher.group().replace(prefix, EMPTY).replace(SINGLE_QUOTE, EMPTY);
////                set.add("https://www.curseforge.com/sims4/create-a-sim/" + match);
////            }
//            return String.join(NEW_LINE, set);
//        } catch (Exception ex) {
//            return "error";
//        }
//
//    }

    @GetMapping("/parseCreator")
    public String parseCreator(@RequestParam String creator) {
        try {
            //https://miikocc.my.curseforge.com/?projectsPage=3&projectsSearch=&projectsSort=9
            //https://alenaivanisova.my.curseforge.com/?projectsPage=14&projectsSearch=&projectsSort=9
//            String string = Str ing.format("https://%s.my.curseforge.com/?projectsPage=XXX&projectsSearch=&projectsSort=9", creator);
//            Set<String> set = getEdgeLinks(string.replace("XXX", "%d"),1, new HashSet<>());

            String url = String.format("https://%s.my.curseforge.com/?", creator);
            String parameters = "projectsPage=%d&projectsSearch=&projectsSort=9";
            List<String> list = getCreatorLinks(url + parameters,1, new ArrayList<>());
            if (list.isEmpty()) return getContent(url + parameters, 1, "member");
            return String.join(NEW_LINE, list);
        } catch (Exception ex) {
            return "error";
        }
    }

    @GetMapping("/parseMember")
    public String parseMember(@RequestParam String member) {
        try {
            //https://www.curseforge.com/members/ssalon1/projects?page=7&pageSize=20&sortBy=ReleaseDate&sortOrder=Desc
            String url = String.format("https://www.curseforge.com/members/%s/projects?", member);
            String parameters = "page=%d&pageSize=20&sortBy=ReleaseDate&sortOrder=Desc";
            List<String> list = getMemberLinks(url + parameters,1, new ArrayList<>());

            if (list.isEmpty()) return getContent(url + parameters, 1, "member");

            return String.join(NEW_LINE, list);
        } catch (Exception ex) {
            return "error";
        }
    }

    private List<String> getMemberLinks(String member, int page, List<String> list) throws Exception {
        String content = getContent(member, page, "member");
        List<String> links = StringUtility.getSetBetweenRegex(content, "btn-cta\" href=\"/", "\">").stream()
                .filter(str -> str.contains("download"))
                .map(str -> CURSE_FORGE.httpUrl + str)
                .toList();
        if (links.isEmpty()) {
            return list;
        } else {
            list.addAll(links);
            return getMemberLinks(member, page + 1, list);
        }
    }

    private List<String> getCreatorLinks(String creator, int page, List<String> list) throws Exception {
        String content = getContent(creator, page, "creator");
        List<String> links = StringUtility.getSetBetweenRegex(content, "\"downloadLink\":\"", SINGLE_QUOTE)
                .stream()
//                .filter(str -> str.contains("download"))
                .map(str -> str.replaceAll("\\\\", EMPTY))
                .toList();
        if (links.isEmpty()) {
            return list;
        } else {
            list.addAll(links);
            return getCreatorLinks(creator, page + 1, list);
        }
    }

    private String getContent(String path, int page, String type) throws Exception {
        String searchURL = String.format(path, page);
        URL url = URLUtility.createURL(searchURL);
        log.info("searching {} url: {}", type, url);
        return OkHttpUtility.getContent(url, client);
    }


//    https://www.curseforge.com/members/ssalon1/projects?page=7&pageSize=20&sortBy=ReleaseDate&sortOrder=Desc

//    private Set<String> getCurseForgeLinks(String creator, int page, Set<String> previous) throws Exception {
//        //https://alenaivanisova.my.curseforge.com/?projectsPage=14&projectsSearch=&projectsSort=9
//        String searchURL = String.format(creator, page);
//        Set<String> result = getLink(searchURL, "href=\"/sims4/create-a-sim/", "/download/");
//        if (result.isEmpty()) {
//            return previous;
//        } else {
//            result.addAll(previous);
//            return getEdgeLinks(creator,page + 1, result);
//        }
//    }
//    private Set<String> getEdgeLinks(String creator, int page, Set<String> previous) throws Exception {
//        //https://alenaivanisova.my.curseforge.com/?projectsPage=14&projectsSearch=&projectsSort=9
//        String searchURL = String.format(creator, page);
//        Set<String> result = getLink(searchURL,"\"downloadLink\":\"", SINGLE_QUOTE);
//        if (result.isEmpty()) {
//            return previous;
//        } else {
//            result.addAll(previous);
//            return getEdgeLinks(creator,page + 1, result);
//        }
//    }
//
//    private Set<String> getLink(String searchURL, String prefix, String suffix) throws Exception {
//        URL url = URLUtility.createURL(searchURL);
//        log.info("curse forge search url: {}", url);
//        String content = OkHttpUtility.getContent(url, client);
//        log.info(content);
//        Matcher matcher = StringUtility.getRegexBetweenMatcher(content, prefix, suffix);
//        Set<String> set = new HashSet<>();
//        while (matcher.find()) {
//            String match = matcher.group()
//                    .replace(prefix, EMPTY)
//                    .replace(suffix, EMPTY)
//                    .replaceAll("\\\\", EMPTY);
//            set.add(match);
//        }
//        return set;
//    }

//    private Set<String> getEdgeLink(String searchURL, String prefix, String suffix) throws Exception {
//        URL url = URLUtility.createURL(searchURL);
//        log.info("curse forge search url: {}", url);
//        String content = OkHttpUtility.getContent(url, client);
//        log.info(content);
//        Matcher matcher = StringUtility.getRegexBetweenMatcher(content, prefix, suffix);
//        Set<String> set = new HashSet<>();
//        while (matcher.find()) {
//            String match = matcher.group()
//                    .replace(prefix, EMPTY)
//                    .replace(suffix, EMPTY)
//                    .replaceAll("\\\\", EMPTY);
//            set.add(match);
//        }
//        return set;
//    }

    @PostMapping("/edge")
    public String edge(@RequestParam String location, @RequestBody String body) {
        File file, directory = new File(location);
        try {
            String[] parts, strings = body.split(NEW_LINE);
            URL url;
            boolean bool;
            List<String> list = new ArrayList<>();
            DownloadResponse response;
            for (String string: strings) {
                parts = string.split("/");
                file = new File(directory, parts[parts.length - 1]);
                url = URLUtility.createURL(string);
                bool = URLUtility.download(url, file);
                response = new DownloadResponse(bool, url);
                list.add(response.toString());
            }
            ConsolidateUtility.consolidate(directory);
            FileUtility.deleteNonPackageFiles(directory);
            return String.join(NEW_LINE, list);
        } catch (Exception ex) {
            return "error";
        }
    }


}
