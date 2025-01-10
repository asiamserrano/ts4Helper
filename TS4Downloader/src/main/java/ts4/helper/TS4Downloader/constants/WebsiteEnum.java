package ts4.helper.TS4Downloader.constants;

public enum WebsiteEnum {
    CURSE_FORGE("www.curseforge.com"),
    PATREON("www.patreon.com");

    private final String url;

    WebsiteEnum(String url) {
        this.url = url;
    }

    public static WebsiteEnum contains(String url) {
        for (WebsiteEnum websiteEnum : WebsiteEnum.values()) {
            if (url.contains(websiteEnum.getUrl())) {
                return websiteEnum;
            }
        }
        return null;
    }

    public String getUrl() {
        return this.url;
    }

}
