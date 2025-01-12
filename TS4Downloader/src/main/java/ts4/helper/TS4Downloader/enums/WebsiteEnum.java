package ts4.helper.TS4Downloader.enums;

import lombok.AllArgsConstructor;

@AllArgsConstructor
public enum WebsiteEnum {
    CURSE_FORGE("www.curseforge.com"),
    PATREON("www.patreon.com"),
    SIMS_FINDS("www.simsfinds.com");

    public final String url;

    public static WebsiteEnum contains(String url) {
        for (WebsiteEnum websiteEnum : WebsiteEnum.values()) {
            if (url.contains(websiteEnum.url)) {
                return websiteEnum;
            }
        }
        return null;
    }

}
