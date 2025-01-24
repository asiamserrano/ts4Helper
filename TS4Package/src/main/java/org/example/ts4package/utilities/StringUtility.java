package org.example.ts4package.utilities;

import com.google.common.io.Resources;
import lombok.extern.slf4j.Slf4j;

import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static org.example.ts4package.constants.StringConstants.EMPTY;
import static org.example.ts4package.constants.StringConstants.SINGLE_QUOTE;
import static org.example.ts4package.constants.StringConstants.STANDARD_REGEX;

@Slf4j
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

    public static String loadResource(String resource) {
        try {
            URL contentURL = Resources.getResource(resource);
            return Resources.toString(contentURL, StandardCharsets.UTF_8).strip();
        } catch (Exception e) {
            log.error("unable to locate resource: {}", resource, e);
            throw new RuntimeException(e);
        }
    }

    private static final List<String> ESCAPE_CHARACTERS = new ArrayList<>() {{
        add("{"); add("}"); add("("); add(")"); add("&");
    }};

    private static String prepareForRegex(String string) {
        for (String character : ESCAPE_CHARACTERS) {
            String escaped = String.format("\\%s", character);
            string = string.replace(character, escaped);
        }
        return string;
    }

}
