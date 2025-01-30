package org.projects.ts4.consumer.enums;

import lombok.AllArgsConstructor;
import okhttp3.OkHttpClient;
import org.projects.ts4.avro.WebsiteModel;
import org.projects.ts4.utility.constructors.WebsiteDomain;
import org.projects.ts4.utility.enums.ResponseEnum;
import org.projects.ts4.utility.utilities.OkHttpUtility;
import org.projects.ts4.utility.utilities.StringUtility;

import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

import static org.projects.ts4.utility.constants.StringConstants.*;
import static org.projects.ts4.utility.constants.StringConstants.EMPTY;
import static org.projects.ts4.utility.constructors.SubDomain.WWW;
import static org.projects.ts4.utility.constructors.SecondLevelDomain.PATREON;
import static org.projects.ts4.utility.constructors.TopLevelDomain.COM;

public class PatreonEnum extends BaseEnumImpl {

    private static final OkHttpClient PATREON_CLIENT = new OkHttpClient();
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

    public static PatreonEnum valueOf(WebsiteModel websiteModel) {
        BaseEnumImpl baseEnumImpl = BaseEnumImpl.valueOf(websiteModel);
        return baseEnumImpl == null ? null : baseEnumImpl instanceof PatreonEnum ? (PatreonEnum) baseEnumImpl : null;
    }

    @Override
    public void parse(WebsiteModel websiteModel) {
        try {
            if (this == PatreonEnum.PATREON_FILE) {
                BaseEnum.print(websiteModel, ResponseEnum.DOWNLOAD);
            } else {
                String content = OkHttpUtility.getContent(websiteModel, PATREON_CLIENT);
                String folder = StringUtility.getStringBetweenRegex(content, "<title>", "</title>");
                websiteModel.setFilename(folder);
                List<WebsiteModel> models = StringUtility.getSetBetweenRegex(content, "{\"attributes\":{\"name\":\"", "\"},")
                        .parallelStream().map(s -> {
                            String name = s.split(",")[0];
                            String string = s.split("url:")[1].replace("\\u0026i", "&i");
                            WebsiteModel singleton = new WebsiteModel();
                            singleton.setUrl(string);
                            singleton.setFilename(name);
                            singleton.setPrevious(websiteModel);
                            return singleton;
                        }).toList();
                if (models.isEmpty()) {
                    BaseEnum.print(websiteModel, ResponseEnum.ERROR);
                } else {
                    models.forEach(PatreonEnum.PATREON_FILE::parse);
                }
            }
        } catch (Exception e) {
            BaseEnum.print(websiteModel, ResponseEnum.ERROR);
        }
    }

    
}
