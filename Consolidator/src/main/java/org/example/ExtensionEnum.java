package org.example;

public enum ExtensionEnum {
    ZIP("zip", ".zip"),
    PACKAGE("octet-stream", ".package");

    public final String type;
    public final String extension;

    ExtensionEnum(String type, String extension) {
        this.type = type;
        this.extension = extension;
    }

}
