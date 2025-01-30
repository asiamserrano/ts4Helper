package org.projects.ts4.utility.constructors;

public class TopLevelDomain extends DomainImpl {

    public static final TopLevelDomain COM = new TopLevelDomain("com");
    public static final TopLevelDomain NET = new TopLevelDomain("net");

    public TopLevelDomain(String value) {
        super(value);
    }

}