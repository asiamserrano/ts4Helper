package org.projects.ts4.consumer.enums;

import lombok.extern.slf4j.Slf4j;
import org.projects.ts4.avro.WebsiteModel;
import org.projects.ts4.utility.constructors.WebsiteDomain;
import org.projects.ts4.utility.utilities.StringUtility;

import java.util.ArrayList;
import java.util.List;

@Slf4j
public abstract class BaseEnumImpl implements BaseEnum {

    public final WebsiteDomain websiteDomain;
    public final String parameter;
    public final String value;

    protected BaseEnumImpl(String value, WebsiteDomain websiteDomain, String parameter) {
        this.value = value;
        this.websiteDomain = websiteDomain;
        this.parameter = parameter;
    }

    public static BaseEnumImpl valueOf(WebsiteModel websiteModel) {
        for (BaseEnumImpl baseEnumImpl : all()) {
            if (baseEnumImpl.isValid(websiteModel.getUrl())) return baseEnumImpl;
        }
        return null;
    }

    @Override
    public String getValue() {
        return this.value;
    }

    public String getStringBetweenTitleHeader(String content) {
        return StringUtility.getStringBetweenRegex(content, "<title>", "</title>");
    }

    private boolean isValid(String url) {
        return url.contains(websiteDomain.toString()) && url.contains(parameter);
    }

    private static List<BaseEnumImpl> all() {
        return new ArrayList<>() {{
            addAll(CurseForgeEnum.ALL);
            addAll(PatreonEnum.ALL);
            addAll(SimsFindsEnum.ALL);
        }};
    }

}
