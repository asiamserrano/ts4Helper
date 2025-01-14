package ts4.helper.TS4Downloader;

import ts4.helper.TS4Downloader.utilities.StringUtility;

import java.util.*;
import java.util.regex.Matcher;

import static ts4.helper.TS4Downloader.constants.StringConstants.*;

public class Main {

    public static void main(String[] args) throws Exception {
        String content = StringUtility.loadResource("html_file.html");

        // https://www.patreon.com/file?h=56379158&m=122652383
        // <a href="/file?h=56379158&amp;m=122652383"

        Set<String> set = StringUtility.getSetBetweenRegex(content, "<a href=\"/file?", SINGLE_QUOTE);
        List<String> list = set.stream()
                .map(s -> "https://www.patreon.com/file?" + s.replace("amp;", EMPTY))
                .toList();

        for (String s : list) System.out.println(s);

//        String[] lines = content.split(NEW_LINE);
//        List<String> list = new ArrayList<>();
//        for (String line : lines) {
//            if (!line.startsWith("#")) {
//                list.add(String.format("\"%s\"", line));
//            }
//        }
//
//        System.out.println(String.join(",\n", list));

//        String prefix = "<a href=\"/sims4/create-a-sim/";
//        Matcher matcher = StringUtility.getRegexBetweenMatcher(content, prefix, SINGLE_QUOTE);
//        Set<String> set = new HashSet<>();
//        while (matcher.find()) {
//            String match = matcher.group().replace(prefix, EMPTY).replace(SINGLE_QUOTE, EMPTY);
//            System.out.println("https://www.curseforge.com/sims4/create-a-sim/" + match);
//        }

//        List<String> list = new ArrayList<>(set);
//        Collections.sort(list);
//        for (String string: list) System.out.println(string);

    }

}
