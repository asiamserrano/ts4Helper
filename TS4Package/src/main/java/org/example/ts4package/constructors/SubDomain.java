package org.example.ts4package.constructors;

public class SubDomain extends Domain {

    public static final SubDomain WWW = new SubDomain("www");
    public static final SubDomain EDGE = new SubDomain("edge");
    public static final SubDomain MY = new SubDomain("my");
    public static final SubDomain CLICK = new SubDomain("click");

    public SubDomain(String value) {
        super(value);
    }

}