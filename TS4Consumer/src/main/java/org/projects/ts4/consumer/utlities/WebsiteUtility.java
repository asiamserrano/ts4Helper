package org.projects.ts4.consumer.utlities;

import org.projects.ts4.avro.WebsiteModel;
import org.projects.ts4.consumer.enums.BaseEnum;
import org.projects.ts4.consumer.enums.BaseEnumImpl;
import org.projects.ts4.utility.enums.ResponseEnum;
import org.projects.ts4.utility.utilities.StringUtility;

import java.util.List;

public class WebsiteUtility {

    public static void main(String[] args) {
        List<String> strings = StringUtility.loadResourceList("files/master_input.txt");
        for(String string : strings) {
            WebsiteModel websiteModel = new WebsiteModel();
            websiteModel.setUrl(string);
            consume(websiteModel);
        }
    }

    public static void consume(WebsiteModel websiteModel) {
        BaseEnumImpl baseEnumImpl = BaseEnumImpl.valueOf(websiteModel);
        if (baseEnumImpl == null) {
            BaseEnum.print(websiteModel, ResponseEnum.UNKNOWN);
        } else {
            baseEnumImpl.parse(websiteModel);
        }
    }


}
