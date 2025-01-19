package ts4.helper.TS4Downloader.enums;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import okhttp3.HttpUrl;
import ts4.helper.TS4Downloader.constructors.DomainPath;
import ts4.helper.TS4Downloader.constructors.SecondLevelDomain;
import ts4.helper.TS4Downloader.constructors.SubDomain;
import ts4.helper.TS4Downloader.constructors.TopLevelDomain;

import java.net.URL;

import static ts4.helper.TS4Downloader.constants.OkHttpConstants.HTTPS_SCHEME;

import static ts4.helper.TS4Downloader.constructors.DomainPath.*;
import static ts4.helper.TS4Downloader.constructors.SubDomain.*;
import static ts4.helper.TS4Downloader.constructors.TopLevelDomain.*;

@Slf4j
@AllArgsConstructor
public enum WebsiteEnum {

    // html source
    PATREON_POSTS(WWW, SecondLevelDomain.PATREON, COM, POSTS), // -> [PATREON_FILE]
    PATREON_FILE(WWW, SecondLevelDomain.PATREON, COM, FILE), // DOWNLOAD

    SIMS_FINDS_DOWNLOADS(WWW, SecondLevelDomain.SIMS_FINDS, COM, DOWNLOADS), // -> SIMS_FINDS_CONTINUE
    SIMS_FINDS_CONTINUE(WWW, SecondLevelDomain.SIMS_FINDS, COM, CONTINUE), // -> SIMS_FINDS_DOWNLOAD
    SIMS_FINDS_DOWNLOAD(CLICK, SecondLevelDomain.SIMS_FINDS, COM, DomainPath.DOWNLOAD), // -> stream OR external file

    CURSE_FORGE_CAS(WWW, SecondLevelDomain.CURSE_FORGE, COM, S4_CAS), // -> CURSE_FORGE_API
    CURSE_FORGE_API(WWW, SecondLevelDomain.CURSE_FORGE, COM, API_V1_MODS), // DOWNLOAD
    CURSE_FORGE_CDN(EDGE, SecondLevelDomain.FORGE_CDN, NET, FILES), //
    CURSE_FORGE_CREATORS(MY, SecondLevelDomain.CURSE_FORGE, COM, NONE), // [CURSE_FORGE_CDN]
    CURSE_FORGE_MEMBERS(WWW, SecondLevelDomain.CURSE_FORGE, COM, MEMBERS); //[CURSE_FORGE_CAS]

    public final SubDomain subDomain;
    public final SecondLevelDomain secondLevelDomain;
    public final TopLevelDomain topLevelDomain;
    public final DomainPath domainPath;

    public static WebsiteEnum getByURL(URL url) {
        for (WebsiteEnum websiteEnum : values()) {
            if (url.toString().contains(websiteEnum.toString())) return websiteEnum;
        }
        return null;
    }

    public HttpUrl getHttpUrl() {
        String host = getHost();
        return new HttpUrl.Builder()
                .scheme(HTTPS_SCHEME)
                .host(host)
                .addPathSegments(domainPath.value)
                .build();
    }

    public String getHost() {
        return String.format("%s.%s.%s", subDomain, secondLevelDomain, topLevelDomain);
    }

    @Override
    public String toString() {
        return this.getHttpUrl().toString().split("//")[1];
    }

}