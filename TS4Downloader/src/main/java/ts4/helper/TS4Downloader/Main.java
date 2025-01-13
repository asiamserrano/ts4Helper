package ts4.helper.TS4Downloader;

import ts4.helper.TS4Downloader.utilities.StringUtility;

import java.util.*;
import java.util.regex.Matcher;

import static ts4.helper.TS4Downloader.constants.StringConstants.EMPTY;
import static ts4.helper.TS4Downloader.constants.StringConstants.SINGLE_QUOTE;

public class Main {

    public static void main(String[] args) throws Exception {
        String content = StringUtility.loadResource("html_file.html");
        String prefix = "<a href=\"/sims4/create-a-sim/";
        Matcher matcher = StringUtility.getRegexBetweenMatcher(content, prefix, SINGLE_QUOTE);
        Set<String> set = new HashSet<>();
        while (matcher.find()) {
            String match = matcher.group().replace(prefix, EMPTY).replace(SINGLE_QUOTE, EMPTY);
            System.out.println("https://www.curseforge.com/sims4/create-a-sim/" + match);
        }

//        List<String> list = new ArrayList<>(set);
//        Collections.sort(list);
//        for (String string: list) System.out.println(string);

    }

}
