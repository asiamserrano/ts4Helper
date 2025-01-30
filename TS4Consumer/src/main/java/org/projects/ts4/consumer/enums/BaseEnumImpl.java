package org.projects.ts4.consumer.enums;

import org.projects.ts4.avro.WebsiteModel;
import org.projects.ts4.utility.constructors.Domain;
import org.projects.ts4.utility.constructors.WebsiteDomain;

import java.util.ArrayList;
import java.util.List;

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

    private boolean isValid(String url) {
        return url.contains(websiteDomain.toString()) && url.contains(parameter);
    }

    @Override
    public String getValue() {
        return this.value;
    }

    private static List<BaseEnumImpl> all() {
        return new ArrayList<>() {{
            addAll(CurseForgeEnum.ALL);
            addAll(PatreonEnum.ALL);
            addAll(SimsFindsEnum.ALL);
        }};
    }

}
