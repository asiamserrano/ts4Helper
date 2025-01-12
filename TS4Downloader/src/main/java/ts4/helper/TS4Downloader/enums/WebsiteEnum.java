package ts4.helper.TS4Downloader.enums;

import lombok.AllArgsConstructor;

import java.net.URL;

@AllArgsConstructor
public enum WebsiteEnum {
    CURSE_FORGE("www.curseforge.com"),
    PATREON("www.patreon.com"),
    SIMS_FINDS("www.simsfinds.com");

    public final String url;

    public static WebsiteEnum contains(URL url) {
        for (WebsiteEnum websiteEnum : WebsiteEnum.values()) {
            if (url.getHost().equals(websiteEnum.url)) {
                return websiteEnum;
            }
        }
        return null;
    }

}
