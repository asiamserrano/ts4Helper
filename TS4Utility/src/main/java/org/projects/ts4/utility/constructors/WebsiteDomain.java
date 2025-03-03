package org.projects.ts4.utility.constructors;


import okhttp3.HttpUrl;

import static org.projects.ts4.utility.constants.StringConstants.EMPTY;
import static org.projects.ts4.utility.constants.StringConstants.PERIOD;
import static org.projects.ts4.utility.constants.OkHttpConstants.HTTPS_SCHEME;

public class WebsiteDomain extends DomainImpl {

//    public static void main(String[] args) {
//        WebsiteDomain websiteDomain = new WebsiteDomain(SubDomain.WWW, SecondLevelDomain.CURSE_FORGE, TopLevelDomain.COM);
//        HttpUrl httpUrl = websiteDomain.getHttpUrl("sims4/create-a-sim/ssalon-female-hairstyle-b95");
////        HttpUrl httpUrl = websiteDomain.getHttpUrl();
//
//        System.out.println(httpUrl);
//    }

    public WebsiteDomain(SubDomain subDomain, SecondLevelDomain secondLevelDomain, TopLevelDomain topLevelDomain) {
        super(subDomain + PERIOD + secondLevelDomain + PERIOD + topLevelDomain);
    }

    public HttpUrl getHttpUrl() {
        return getHttpUrl(EMPTY);
    }

    public HttpUrl getHttpUrl(String pathSegments) {
        String host = this.toString();
        return new HttpUrl.Builder()
                .scheme(HTTPS_SCHEME)
                .host(host)
                .addPathSegments(pathSegments)
                .build();
    }

}
