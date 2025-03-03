package org.projects.ts4.consumer.enums;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import okhttp3.Response;
import org.projects.ts4.avro.WebsiteModel;
import org.projects.ts4.consumer.classes.WebsiteLogger;
import org.projects.ts4.utility.classes.Website;
import org.projects.ts4.utility.constructors.SubDomain;
import org.projects.ts4.utility.constructors.WebsiteDomain;
import org.projects.ts4.utility.enums.ExtensionEnum;
import org.projects.ts4.utility.enums.ServiceEnum;
import org.projects.ts4.utility.enums.ResponseEnum;
import org.projects.ts4.utility.utilities.FileUtility;
import org.projects.ts4.utility.utilities.StringUtility;

import java.io.File;
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
        SF_DOWNLOAD(CLICK, "download");
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
        String value = String.format("SF_%s", parameter.toUpperCase());
        return new SimsFindsEnum(value, websiteDomain, parameter, enumeration);
    }

    @Override
    public void parse(WebsiteLogger websiteLogger) {
        WebsiteModel websiteModel = websiteLogger.websiteModel;
        try {
            switch (this.enumeration) {
                case SF_DOWNLOADS: {
                    String content = websiteLogger.getContent();
                    String name = StringUtility
                            .getStringBetweenRegex(content, "<meta name=\"keywords\" content=", COMMA);
                    websiteModel.setFilename(name);
                    String url = StringUtility.getStringBetweenRegex(content, "data-continue=\"", SINGLE_QUOTE);
                    WebsiteModel singleton = Website.build(url, name, websiteModel);
                    SIMS_FINDS_CONTINUE.parse(websiteLogger.create(singleton));
                    break;
                }
                case SF_CONTINUE: {
                    String content = websiteLogger.getContent();
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
                    String url = "https://click.simsfinds.com/download?" + String.join(AMPERSAND, list);
                    String name = websiteModel.getFilename();
                    WebsiteModel singleton = Website.build(url, name, websiteModel);
                    SIMS_FINDS_DOWNLOAD.parse(websiteLogger.create(singleton));
                    break;
                }
                case SF_DOWNLOAD: {
                    String url = websiteModel.getUrl();
                    if (url.contains("flid=0")) {
                        url = getStringBetweenTitleHeader(websiteLogger.getContent());
                        File directory = FileUtility.createDirectory(websiteModel.getDirectory());
                        WebsiteModel singleton = Website.build(url, directory, websiteModel);
                        websiteLogger.send(singleton, ServiceEnum.CONSUMER);
                    } else {
                        Response response = websiteLogger.getResponse();
                        ExtensionEnum extensionEnum = ExtensionEnum.valueOf(response);
                        ResponseEnum responseEnum = ResponseEnum.getResponseEnum(websiteModel, extensionEnum);
                        String extension = ExtensionEnum.getExtension(extensionEnum);
                        String name = websiteModel.getFilename() + extension;
                        WebsiteModel singleton = Website.build(url, name, websiteModel);
                        websiteLogger.create(singleton).print(responseEnum);
                    }
                    break;
                }
            }
        } catch (Exception e) {
            websiteLogger.exception(e);
        }
    }
}
