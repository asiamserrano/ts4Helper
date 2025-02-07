package org.projects.ts4.consumer.enums;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.projects.ts4.avro.WebsiteModel;
import org.projects.ts4.consumer.classes.WebsiteLogger;
import org.projects.ts4.utility.classes.Website;
import org.projects.ts4.utility.constructors.WebsiteDomain;
import org.projects.ts4.utility.enums.ResponseEnum;
import org.projects.ts4.utility.utilities.StringUtility;

import java.util.ArrayList;
import java.util.List;

import static org.projects.ts4.utility.constants.StringConstants.*;
import static org.projects.ts4.utility.constructors.SubDomain.WWW;
import static org.projects.ts4.utility.constructors.SecondLevelDomain.PATREON;
import static org.projects.ts4.utility.constructors.TopLevelDomain.COM;

@Slf4j
public class PatreonEnum extends BaseEnumImpl {

//    public static void main(String[] args) {
//        String content = StringUtility.loadResourceString("files/examples/PATREON_POSTS.html");
//        content = StringUtility.getStringBetweenRegex(content, ">Attachments<", NEW_LINE);
//        StringUtility.getSetBetweenRegex(content, "track-click", "</a></span>").stream().forEach(str -> {
//            String name = StringUtility.last(str, ">");
//            String h = StringUtility.getStringBetweenRegex(str, "h=", AMPERSAND);
//            String m = StringUtility.getStringBetweenRegex(str, "m=", SPACE);
//            String url = String.format("https://www.patreon.com/file?h=%s&m=%s", h, m);
//
//            System.out.println(name);
//            System.out.println(url);
//
//        });
//    }

    private static final WebsiteDomain websiteDomain = new WebsiteDomain(WWW, PATREON, COM);

    public static final PatreonEnum PATREON_POSTS = new PatreonEnum(Enumeration.PATREON_POSTS);
    public static final PatreonEnum PATREON_FILE = new PatreonEnum(Enumeration.PATREON_FILE);

    public static final List<PatreonEnum> ALL = new ArrayList<>() {{
        add(PATREON_POSTS);
        add(PATREON_FILE);
    }};

    public final Enumeration enumeration;

    @AllArgsConstructor
    public enum Enumeration {
        PATREON_POSTS("PATREON_LINK", "/posts/"),
        PATREON_FILE("PATREON_DOWNLOAD", "file?");
        public final String value;
        public final String parameter;
    }

    private PatreonEnum(Enumeration enumeration) {
        super(enumeration.value, websiteDomain, enumeration.parameter);
        this.enumeration = enumeration;
    }

    @Override
    public void parse(WebsiteLogger websiteLogger) {
        WebsiteModel websiteModel = websiteLogger.websiteModel;
        try {
            if (this == PatreonEnum.PATREON_FILE) {
                websiteLogger.print(ResponseEnum.DOWNLOAD);
            } else {
                String content = websiteLogger.getContent();
                String folder = getStringBetweenTitleHeader(content);
                websiteModel.setFilename(folder);
                content = StringUtility.getStringBetweenRegex(content, ">Attachments<", NEW_LINE);
                List<WebsiteModel> models = StringUtility
                        .getSetBetweenRegex(content, "track-click", "</a></span>")
                        .parallelStream().map(str -> {
                            String name = StringUtility.last(str, GREATER_THAN);
                            String h = StringUtility.getStringBetweenRegex(str, "h=", AMPERSAND);
                            String m = StringUtility.getStringBetweenRegex(str, "m=", SPACE);
                            String url = String.format("https://www.patreon.com/file?h=%s&m=%s", h, m);
                            return Website.build(url, name, websiteModel);
                        }).toList();
                if (models.isEmpty()) {
                    log.error("could not find attachments for patreon link: {}", websiteModel.getUrl());
                    websiteLogger.print(ResponseEnum.INVALID);
                } else {
                    models.forEach(model -> PatreonEnum.PATREON_FILE.parse(websiteLogger.create(model)));
                }
            }
        } catch (Exception e) {
            websiteLogger.exception(e);
        }
    }
    
}
