package ts4.helper.TS4Downloader.utilities;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static ts4.helper.TS4Downloader.constants.StringConstants.EMPTY;
import static ts4.helper.TS4Downloader.constants.StringConstants.SINGLE_QUOTE;

import static ts4.helper.TS4Downloader.constants.StringConstants.STANDARD_REGEX;

public abstract class StringUtility {

//    private static final String REGEX = "%s.*?%s";

    public static String format(String format, String a, String b) {
        return String.format(format, a, b);
    }

    public static String regexBetween(String content, String p1, String p2) {
        String s = format(STANDARD_REGEX, prepareForRegex(p1), prepareForRegex(p2));
        Pattern p = Pattern.compile(s);
        Matcher m = p.matcher(content);

        if (m.find()) {
            return m.group().replace(p1, EMPTY).replace(p2, EMPTY).replace(SINGLE_QUOTE, EMPTY);
        } else {
            return EMPTY;
        }
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
