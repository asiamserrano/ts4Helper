package org.projects.ts4.utility.constructors;

public class SecondLevelDomain extends DomainImpl {

    public static final SecondLevelDomain PATREON = new SecondLevelDomain("patreon");
    public static final SecondLevelDomain SIMS_FINDS = new SecondLevelDomain("simsfinds");
    public static final SecondLevelDomain CURSE_FORGE = new SecondLevelDomain("curseforge");
    public static final SecondLevelDomain FORGE_CDN = new SecondLevelDomain("forgecdn");

    public SecondLevelDomain(String value) {
        super(value);
    }

}
