package org.ts4.pkg.constructors;

public class TopLevelDomain extends Domain {

    public static final TopLevelDomain COM = new TopLevelDomain("com");
    public static final TopLevelDomain NET = new TopLevelDomain("net");

    public TopLevelDomain(String value) {
        super(value);
    }

}