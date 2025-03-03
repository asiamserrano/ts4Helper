package org.projects.ts4.consumer.enums;

import org.projects.ts4.consumer.classes.WebsiteLogger;
import org.projects.ts4.utility.constructors.Domain;

public interface BaseEnum extends Domain {

    void parse(WebsiteLogger websiteLogger);

}
