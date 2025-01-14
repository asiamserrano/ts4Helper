package ts4.helper.TS4Downloader.enums;

import lombok.AllArgsConstructor;

@AllArgsConstructor
public enum DomainEnum {

    PATREON("patreon.com"),
    SIMS_FINDS("simsfinds.com"),
    CURSE_FORGE("curseforge.com"),
    FORGE_CDN("forgecdn.net");

    public final String name;

}
