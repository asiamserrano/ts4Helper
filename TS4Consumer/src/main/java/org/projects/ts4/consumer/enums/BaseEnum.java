package org.projects.ts4.consumer.enums;

import org.projects.ts4.avro.WebsiteModel;
import org.projects.ts4.consumer.producers.WebsiteProducer;
import org.projects.ts4.utility.constructors.Domain;

public interface BaseEnum extends Domain {

    void parse(WebsiteModel websiteModel, WebsiteProducer websiteProducer);

}
