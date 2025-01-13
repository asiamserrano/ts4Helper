package ts4.helper.TS4Downloader.utilities;

import com.google.common.io.Resources;

import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static ts4.helper.TS4Downloader.constants.StringConstants.EMPTY;
import static ts4.helper.TS4Downloader.constants.StringConstants.SINGLE_QUOTE;
import static ts4.helper.TS4Downloader.constants.StringConstants.STANDARD_REGEX;

public abstract class StringUtility {

    public static String getStringBetweenRegex(String content, String p1, String p2) {
        Matcher m = getRegexBetweenMatcher(content, p1, p2);
        if (m.find()) {
            return clean(m.group(), p1, p2);
        } else {
            return EMPTY;
        }
    }

    public static Set<String> getSetBetweenRegex(String content, String p1, String p2) {
        Matcher m = getRegexBetweenMatcher(content, p1, p2);
        Set<String> set = new HashSet<>();
        while (m.find()) set.add(clean(m.group(), p1, p2));
        return set;
    }

    private static String clean(String string, String p1, String p2) {
        return string.replace(p1, EMPTY).replace(p2, EMPTY).replace(SINGLE_QUOTE, EMPTY);
    }

    public static Matcher getRegexBetweenMatcher(String content, String p1, String p2) {
        String s = String.format(STANDARD_REGEX, prepareForRegex(p1), prepareForRegex(p2));
        Pattern p = Pattern.compile(s);
        return p.matcher(content);
    }

    public static String loadResource(String resource) throws Exception {
        URL contentURL = Resources.getResource(resource);
        return Resources.toString(contentURL, StandardCharsets.UTF_8).strip();
    }

    private static final List<String> ESCAPE_CHARACTERS = new ArrayList<>() {{
        add("{"); add("}"); add("("); add(")");
    }};

    private static String prepareForRegex(String string) {
        for (String character : ESCAPE_CHARACTERS) {
            String escaped = String.format("\\%s", character);
            string = string.replace(character, escaped);
        }
        return string;
    }

}
