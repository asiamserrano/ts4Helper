package ts4.helper.TS4Downloader.models;

import lombok.AllArgsConstructor;
import ts4.helper.TS4Downloader.enums.WebsiteEnum;

import java.net.URL;
import java.util.HashSet;
import java.util.Set;

import static ts4.helper.TS4Downloader.constants.StringConstants.EMPTY;

import static ts4.helper.TS4Downloader.enums.WebsiteEnum.PATREON_POSTS;
import static ts4.helper.TS4Downloader.enums.WebsiteEnum.SIMS_FINDS_DOWNLOADS;
import static ts4.helper.TS4Downloader.enums.WebsiteEnum.CURSE_FORGE_MEMBERS;
import static ts4.helper.TS4Downloader.enums.WebsiteEnum.CURSE_FORGE_CREATORS;
import static ts4.helper.TS4Downloader.enums.WebsiteEnum.CURSE_FORGE_CAS;

@AllArgsConstructor
public class NestedURL {

    public final URL parent;
    public final URL child;

    private static final Set<WebsiteEnum> PARENT_WEBSITES = new HashSet<>() {{
        add(PATREON_POSTS);
        add(SIMS_FINDS_DOWNLOADS);
        add(CURSE_FORGE_MEMBERS);
        add(CURSE_FORGE_CREATORS);
        add(CURSE_FORGE_CAS);
    }};

    public NestedURL(WebsiteEnum websiteEnum, URL url) {
        if (PARENT_WEBSITES.contains(websiteEnum)) {
            this.parent = url;
            this.child = null;
        } else {
            this.parent = null;
            this.child = url;
        }
    }

    @Override
    public String toString() {
        String parentString = this.parent == null ? EMPTY : this.parent.toString();
        String childString = this.child == null ? EMPTY : this.child.toString();
        return String.format("PARENT=%s | CHILD=%s", parentString, childString);
    }

}
