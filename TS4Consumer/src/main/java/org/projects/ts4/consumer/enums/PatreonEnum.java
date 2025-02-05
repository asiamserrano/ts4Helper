package org.projects.ts4.consumer.enums;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.projects.ts4.avro.WebsiteModel;
import org.projects.ts4.consumer.producers.WebsiteProducer;
import org.projects.ts4.consumer.utlities.WebsiteUtility;
import org.projects.ts4.utility.constructors.WebsiteDomain;
import org.projects.ts4.utility.enums.ResponseEnum;
import org.projects.ts4.utility.utilities.OkHttpUtility;
import org.projects.ts4.utility.utilities.StringUtility;

import java.util.ArrayList;
import java.util.List;

import static org.projects.ts4.utility.constants.StringConstants.COMMA;

import static org.projects.ts4.utility.constructors.SubDomain.WWW;
import static org.projects.ts4.utility.constructors.SecondLevelDomain.PATREON;
import static org.projects.ts4.utility.constructors.TopLevelDomain.COM;

@Slf4j
public class PatreonEnum extends BaseEnumImpl {

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
    public void parse(WebsiteModel websiteModel, WebsiteProducer websiteProducer) {
        try {
            if (this == PatreonEnum.PATREON_FILE) {
                WebsiteUtility.print(websiteModel, ResponseEnum.DOWNLOAD, websiteProducer);
            } else {
                String content = OkHttpUtility.getContent(websiteModel, websiteProducer.okHttpClient);
                String folder = getStringBetweenTitleHeader(content);
                websiteModel.setFilename(folder);
                List<WebsiteModel> models = StringUtility.getSetBetweenRegex(content, "{\"attributes\":{\"name\":\"", "\"},")
                        .parallelStream().map(s -> {
                            String name = s.split(COMMA)[0];
                            String string = s.split("url:")[1].replace("\\u0026i", "&i");
                            WebsiteModel singleton = new WebsiteModel();
                            singleton.setUrl(string);
                            singleton.setFilename(name);
                            singleton.setDirectory(websiteModel.getDirectory());
                            singleton.setPrevious(websiteModel);
                            return singleton;
                        }).toList();
                if (models.isEmpty()) {
                    WebsiteUtility.print(websiteModel, ResponseEnum.ERROR, websiteProducer);
                } else {
                    models.forEach(model -> PatreonEnum.PATREON_FILE.parse(model, websiteProducer));
                }
            }
        } catch (Exception e) {
            WebsiteUtility.print(websiteModel, ResponseEnum.ERROR, websiteProducer);
        }
    }

    
}
