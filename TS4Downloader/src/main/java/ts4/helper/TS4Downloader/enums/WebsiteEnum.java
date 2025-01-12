package ts4.helper.TS4Downloader.enums;

import lombok.AllArgsConstructor;
import okhttp3.HttpUrl;
import ts4.helper.TS4Downloader.utilities.OkHttpUtility;

import java.net.URL;

import static ts4.helper.TS4Downloader.constants.OkHttpConstants.HTTPS_SCHEME;

public enum WebsiteEnum {
    CURSE_FORGE("www.curseforge.com"),
    PATREON("www.patreon.com"),
    SIMS_FINDS("www.simsfinds.com");

    public final String url;
    public final HttpUrl httpUrl;

    WebsiteEnum(String url) {
        this.url = url;
        this.httpUrl = OkHttpUtility.create(HTTPS_SCHEME, this.url);
    }

    public static WebsiteEnum contains(URL url) {
        for (WebsiteEnum websiteEnum : WebsiteEnum.values()) {
            if (url.getHost().equals(websiteEnum.url)) {
                return websiteEnum;
            }
        }
        return null;
    }

}
