package ts4.helper.TS4Downloader.constants;

public abstract class ConfigConstants {
    public static final String PROFILE = "local";
    public static final String SECURITY_CONFIG_POLICY = "script-src 'self'";
    public static final String[] SECURITY_CONFIG_ANT_MATCHERS = new String[] { "/**" };
    public static final String COOKIE_JAR_BEAN = "cookieJar";
    public static final String OK_HTTP_CLIENT_BEAN = "client";
    public static final String NON_DOWNLOADED_LINKS_FILE_BEAN = "nonDownloadedLinksFile";
}
