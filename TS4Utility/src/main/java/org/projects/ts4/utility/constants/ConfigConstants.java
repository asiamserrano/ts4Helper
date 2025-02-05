package org.projects.ts4.utility.constants;

import java.time.ZoneId;

public abstract class ConfigConstants {
    public static final String PROFILE = "local";
    public static final String SECURITY_CONFIG_POLICY = "script-src 'self'";
    public static final String[] SECURITY_CONFIG_ANT_MATCHERS = new String[] { "/**" };
    public static final String COOKIE_JAR_BEAN = "cookieJar";
    public static final String OK_HTTP_CLIENT_BEAN = "client";
    public static final String NON_DOWNLOADED_LINKS_FILE_BEAN = "nonDownloadedLinksFile";

    public static final String SCHEMA_REGISTRY_URL_HEADER = "schema.registry.url";
    public static final String SCHEMA_REGISTRY_URL_VALUE = "http://localhost:8081";
    public static final String AUTO_OFFSET_RESET_VALUE = "earliest";

    public static final ZoneId EST_ZONE_ID = ZoneId.of("America/New_York");

}
