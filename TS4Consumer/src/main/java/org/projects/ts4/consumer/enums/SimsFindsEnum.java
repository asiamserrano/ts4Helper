package org.projects.ts4.consumer.enums;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import okhttp3.OkHttpClient;
import okhttp3.Response;
import org.projects.ts4.avro.WebsiteModel;
import org.projects.ts4.consumer.producers.WebsiteProducer;
import org.projects.ts4.consumer.utlities.WebsiteUtility;
import org.projects.ts4.utility.constructors.SubDomain;
import org.projects.ts4.utility.constructors.WebsiteDomain;
import org.projects.ts4.utility.enums.ExtensionEnum;
import org.projects.ts4.utility.enums.KafkaTopicEnum;
import org.projects.ts4.utility.enums.ResponseEnum;
import org.projects.ts4.utility.utilities.OkHttpUtility;
import org.projects.ts4.utility.utilities.StringUtility;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static org.projects.ts4.utility.constants.StringConstants.AMPERSAND;
import static org.projects.ts4.utility.constants.StringConstants.COMMA;
import static org.projects.ts4.utility.constants.StringConstants.SINGLE_QUOTE;
import static org.projects.ts4.utility.constructors.SecondLevelDomain.SIMS_FINDS;
import static org.projects.ts4.utility.constructors.SubDomain.WWW;
import static org.projects.ts4.utility.constructors.SubDomain.CLICK;
import static org.projects.ts4.utility.constructors.TopLevelDomain.COM;

@Slf4j
public class SimsFindsEnum extends BaseEnumImpl {

    public static final SimsFindsEnum SIMS_FINDS_DOWNLOADS = build(Enumeration.SF_DOWNLOADS);
    public static final SimsFindsEnum SIMS_FINDS_CONTINUE = build(Enumeration.SF_CONTINUE);
    public static final SimsFindsEnum SIMS_FINDS_DOWNLOAD = build(Enumeration.SF_DOWNLOAD);
    
    public final Enumeration enumeration;

    @AllArgsConstructor
    public enum Enumeration {
        SF_DOWNLOADS(WWW, "downloads"),
        SF_CONTINUE(WWW, "continue"),
        SF_DOWNLOAD( CLICK, "download");
        public final SubDomain subDomain;
        public final String parameter;
    }

    public static final List<SimsFindsEnum> ALL = new ArrayList<>() {{
        add(SIMS_FINDS_DOWNLOADS);
        add(SIMS_FINDS_CONTINUE);
        add(SIMS_FINDS_DOWNLOAD);
    }};

    private SimsFindsEnum(String v, WebsiteDomain w, String p, Enumeration enumeration) {
        super(v, w, p);
        this.enumeration = enumeration;
    }

    private static SimsFindsEnum build(Enumeration enumeration) {
        WebsiteDomain websiteDomain = new WebsiteDomain(enumeration.subDomain, SIMS_FINDS, COM);
        String parameter = enumeration.parameter;
        String value = parameter.toUpperCase();
        return new SimsFindsEnum(value, websiteDomain, parameter, enumeration);
    }

    @Override
    public void parse(WebsiteModel websiteModel, WebsiteProducer websiteProducer) {
        OkHttpClient okHttpClient = websiteProducer.okHttpClient;
        try {
            switch (this.enumeration) {
                case SF_DOWNLOADS: {
                    String content = OkHttpUtility.getContent(websiteModel, okHttpClient);
                    String name = StringUtility.getStringBetweenRegex(content, "<meta name=\"keywords\" content=", COMMA);
                    websiteModel.setFilename(name);
                    String cont = StringUtility.getStringBetweenRegex(content, "data-continue=\"", SINGLE_QUOTE);
                    WebsiteModel singleton = new WebsiteModel();
                    singleton.setUrl(cont);
                    singleton.setFilename(name);
                    singleton.setPrevious(websiteModel);
                    singleton.setDirectory(websiteModel.getDirectory());
                    SimsFindsEnum.SIMS_FINDS_CONTINUE.parse(singleton, websiteProducer);
                    break;
                }
                case SF_CONTINUE: {
                    String content = OkHttpUtility.getContent(websiteModel, okHttpClient);
                    String flid = StringUtility.getStringBetweenRegex(content, "data-at8r136r7=\"", SINGLE_QUOTE);
                    String pass = StringUtility.getStringBetweenRegex(content, "data-passe=\"", SINGLE_QUOTE);
                    String[] info = StringUtility.getStringBetweenRegex(content, "data-at5t768r9=\"", SINGLE_QUOTE)
                            .split(COMMA);
                    Map<String, String> map = new HashMap<>() {{
                        put("cid", info[0]);
                        put("key", info[1]);
                        put("version", info[3]);
                        put("pass", pass);
                        put("flid", flid);
                    }};
                    List<String> list = map.entrySet().parallelStream()
                            .map(e -> String.format("%s=%s", e.getKey(), e.getValue()))
                            .collect(Collectors.toList());
                    String downloadURLString = "https://click.simsfinds.com/download?" + String.join(AMPERSAND, list);
                    WebsiteModel singleton = new WebsiteModel();
                    singleton.setUrl(downloadURLString);
                    singleton.setFilename(websiteModel.getFilename());
                    singleton.setPrevious(websiteModel);
                    singleton.setDirectory(websiteModel.getDirectory());
                    SimsFindsEnum.SIMS_FINDS_DOWNLOAD.parse(singleton, websiteProducer);
                    break;
                }
                case SF_DOWNLOAD: {
                    String newURL = websiteModel.getUrl();
                    if (newURL.contains("flid=0")) {
                        String content = OkHttpUtility.getContent(websiteModel, okHttpClient);
                        String urlString = getStringBetweenTitleHeader(content);
                        WebsiteModel singleton = new WebsiteModel();
                        singleton.setUrl(urlString);
                        singleton.setPrevious(websiteModel);
                        singleton.setDirectory(websiteModel.getDirectory());
                        // TODO: change this, don't assume it goes somewhere I can't download
                        // this should send a kafka message to this same consumer
//                        WebsiteUtility.print(singleton, ResponseEnum.UNKNOWN, websiteProducer);
                        websiteProducer.send(singleton, KafkaTopicEnum.CONSUMER);
                    } else {
                        Response response = OkHttpUtility.sendRequest(websiteModel, okHttpClient);
                        ExtensionEnum extensionEnum = ExtensionEnum.valueOf(response);
                        ResponseEnum responseEnum = ResponseEnum.getResponseEnum(websiteModel, extensionEnum);
                        String extension = ExtensionEnum.getExtension(extensionEnum);
                        String filename = websiteModel.getFilename() + extension;
                        WebsiteModel singleton = new WebsiteModel();
                        singleton.setUrl(newURL);
                        singleton.setFilename(filename);
                        singleton.setPrevious(websiteModel.getPrevious());
                        singleton.setDirectory(websiteModel.getDirectory());
                        WebsiteUtility.print(singleton, responseEnum, websiteProducer);
                    }
                    break;
                }
            }
        } catch (Exception e) {
            WebsiteUtility.print(websiteModel, ResponseEnum.ERROR, websiteProducer);
        }
    }

}
