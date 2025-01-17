package ts4.helper.TS4Downloader.constructors;

import ts4.helper.TS4Downloader.constants.StringConstants;

public class DomainPath extends Domain {

    public static final DomainPath NONE = new DomainPath(StringConstants.EMPTY);
    public static final DomainPath POSTS = new DomainPath("posts");
    public static final DomainPath MEMBERS = new DomainPath("members");
    public static final DomainPath DOWNLOADS = new DomainPath("downloads");
    public static final DomainPath S4_CAS = new DomainPath("sims4/create-a-sim");
    public static final DomainPath API_V1_MODS = new DomainPath("api/v1/mods");
    public static final DomainPath FILES = new DomainPath("files");
    public static final DomainPath FILE = new DomainPath("file");
    public static final DomainPath DOWNLOAD = new DomainPath("download");
    public static final DomainPath CONTINUE = new DomainPath("continue");
    
    public DomainPath(String value) {
        super(value);
    }
    
}
