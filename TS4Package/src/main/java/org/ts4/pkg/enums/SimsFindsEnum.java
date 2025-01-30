package org.ts4.pkg.enums;

import lombok.AllArgsConstructor;

@AllArgsConstructor
public enum SimsFindsEnum {
    DOWNLOADS("downloads/"),
    CONTINUE("continue?"),
    DOWNLOAD("download?");

    public final String url_delimiter;

}
