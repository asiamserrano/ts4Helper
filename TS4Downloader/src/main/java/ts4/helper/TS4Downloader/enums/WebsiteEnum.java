package ts4.helper.TS4Downloader.enums;

import java.net.URL;

import static ts4.helper.TS4Downloader.enums.DomainEnum.PATREON;
import static ts4.helper.TS4Downloader.enums.DomainEnum.SIMS_FINDS;
import static ts4.helper.TS4Downloader.enums.DomainEnum.CURSE_FORGE;
import static ts4.helper.TS4Downloader.enums.DomainEnum.FORGE_CDN;

public enum WebsiteEnum {

    PATREON_POSTS("www.%s/posts/", PATREON),
    PATREON_FILE("www.%s/file?", PATREON),
    SIMS_FINDS_DOWNLOADS("www.%s/downloads/",SIMS_FINDS),
    SIMS_FINDS_CONTINUE("www.%s/continue?", SIMS_FINDS),
    SIMS_FINDS_DOWNLOAD("click.%s/download?", SIMS_FINDS),
    CURSE_FORGE_CAS("www.%s/sims4/create-a-sim/", CURSE_FORGE),
    CURSE_FORGE_MEMBERS("www.%s/members/", CURSE_FORGE),
    CURSE_FORGE_CREATORS("my.%s/?", CURSE_FORGE),
    CURSE_FORGE_API("www.%s/api/v1/mods/", CURSE_FORGE),
    CURSE_FORGE_CDN("edge.%s/files/", FORGE_CDN);

    public final String domain;
    public final DomainEnum domainEnum;

    WebsiteEnum(String format, DomainEnum domainEnum) {
        this.domain = String.format(format, domainEnum.name);
        this.domainEnum = domainEnum;
    }

    public static WebsiteEnum getByURL(URL url) {
        for (WebsiteEnum websiteEnum : values()) {
            if (url.toString().contains(websiteEnum.domain)) return websiteEnum;
        }
        return null;
    }

//    PATREON("patreon.com", Arrays.asList("www.%s/posts/", "www.%s/file?")),
//    SIMS_FINDS("simsfinds.com", Arrays.asList("www.%s/downloads/", "www.%s/continue?", "click.%s/download?")),
//    CURSE_FORGE("curseforge.com", Arrays.asList("www.%s/sims4/create-a-sim/",
//            "www.%s/members/", "my.%s/?", "www.%s/api/v1/mods/")),
//    FORGE_CDN("forgecdn.net", List.of("edge.%s/files/"));

//    patreon.com/posts/
//    patreon.com/file?
//
//    simsfinds.com/downloads/
//    simsfinds.com/continue?
//    click.simsfinds.com/download?
//
//    curseforge.com/sims4/create-a-sim/
//    curseforge.com/members/
//    my.curseforge.com/?
//    curseforge.com/api/v1/mods/
//
//    edge.forgecdn.net/files/

}
