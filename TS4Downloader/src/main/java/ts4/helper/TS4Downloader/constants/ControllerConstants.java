package ts4.helper.TS4Downloader.constants;

public abstract class ControllerConstants {
    public static final String EVENT_CONTROLLER_REQUEST_MAPPING = "/event";
    public static final String EVENT_CONTROLLER_SAMPLE_GET_MAPPING = "/sample";
    public static final String EVENT_CONTROLLER_DOWNLOAD_LINKS_POST_MAPPING = "/downloadLinks";
    public static final String EVENT_CONTROLLER_CONSOLIDATE_POST_MAPPING = "/consolidate";

    public static final int EVENT_CONTROLLER_THREAD_POOL_SIZE = 5;

    public static final String CURSE_FORGE_CONTROLLER_REQUEST_MAPPING = "/curseForge";
    public static final String CURSE_FORGE_CONTROLLER_COOKIE_STATUS_GET_MAPPING = "/cookieStatus";
    public static final String CURSE_FORGE_CONTROLLER_UPDATE_COOKIE_POST_MAPPING = "/updateCookie";

}
