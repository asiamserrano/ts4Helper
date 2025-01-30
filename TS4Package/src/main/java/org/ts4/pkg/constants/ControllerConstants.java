package org.ts4.pkg.constants;

import java.time.format.DateTimeFormatter;

public abstract class ControllerConstants {
    public static final String EVENT_CONTROLLER_REQUEST_MAPPING = "/event";
    public static final String EVENT_CONTROLLER_SAMPLE_GET_MAPPING = "/sample";
    public static final String EVENT_CONTROLLER_DOWNLOAD_LINKS_POST_MAPPING = "/downloadLinks";
    public static final String EVENT_CONTROLLER_CONSOLIDATE_POST_MAPPING = "/consolidate";

    public static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd_HH-mm-ss");

    public static final String CURSE_FORGE_CONTROLLER_REQUEST_MAPPING = "/curseForge";
    public static final String CURSE_FORGE_CONTROLLER_COOKIE_STATUS_GET_MAPPING = "/cookieStatus";
    public static final String CURSE_FORGE_CONTROLLER_UPDATE_COOKIE_POST_MAPPING = "/updateCookie";

}
