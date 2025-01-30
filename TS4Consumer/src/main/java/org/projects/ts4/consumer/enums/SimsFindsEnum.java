package org.projects.ts4.consumer.enums;

import lombok.AllArgsConstructor;
import okhttp3.OkHttpClient;
import okhttp3.Response;
import org.projects.ts4.avro.WebsiteModel;
import org.projects.ts4.utility.constructors.SubDomain;
import org.projects.ts4.utility.constructors.WebsiteDomain;
import org.projects.ts4.utility.enums.ExtensionEnum;
import org.projects.ts4.utility.enums.ResponseEnum;
import org.projects.ts4.utility.utilities.OkHttpUtility;
import org.projects.ts4.utility.utilities.StringUtility;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static org.projects.ts4.utility.constants.StringConstants.*;
import static org.projects.ts4.utility.constructors.SecondLevelDomain.SIMS_FINDS;
import static org.projects.ts4.utility.constructors.SubDomain.WWW;
import static org.projects.ts4.utility.constructors.SubDomain.CLICK;
import static org.projects.ts4.utility.constructors.TopLevelDomain.COM;

public class SimsFindsEnum extends BaseEnumImpl {

    public static final OkHttpClient SIMS_FINDS_CLIENT = new OkHttpClient();
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

    public static SimsFindsEnum valueOf(WebsiteModel websiteModel) {
        BaseEnumImpl baseEnumImpl = BaseEnumImpl.valueOf(websiteModel);
        return baseEnumImpl == null ? null : baseEnumImpl instanceof SimsFindsEnum ? (SimsFindsEnum) baseEnumImpl : null;
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
    public void parse(WebsiteModel websiteModel) {
        try {
            switch (this.enumeration) {
                case SF_DOWNLOADS: {
                    String content = OkHttpUtility.getContent(websiteModel, SIMS_FINDS_CLIENT);
                    String name = StringUtility.getStringBetweenRegex(content, "<title id=\"title\">", "</title>");
                    websiteModel.setFilename(name);
                    String cont = StringUtility.getStringBetweenRegex(content, "data-continue=\"", "\"");
                    WebsiteModel singleton = new WebsiteModel();
                    singleton.setUrl(cont);
                    singleton.setFilename(name);
                    singleton.setPrevious(websiteModel);
                    SimsFindsEnum.SIMS_FINDS_CONTINUE.parse(singleton);
                    break;
                }
                case SF_CONTINUE: {
                    String content = OkHttpUtility.getContent(websiteModel, SIMS_FINDS_CLIENT);
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
                    SimsFindsEnum.SIMS_FINDS_DOWNLOAD.parse(singleton);
                    break;
                }
                case SF_DOWNLOAD: {
                    String newURL = websiteModel.getUrl();
                    if (newURL.contains("flid=0")) {
                        String content = OkHttpUtility.getContent(websiteModel, SIMS_FINDS_CLIENT);
                        String urlString = StringUtility.getStringBetweenRegex(content, "<title>", "</title");
                        WebsiteModel singleton = new WebsiteModel();
                        singleton.setUrl(urlString);
                        singleton.setPrevious(websiteModel);
                        BaseEnum.print(singleton, ResponseEnum.UNKNOWN);
                    } else {
                        Response response = OkHttpUtility.sendRequest(websiteModel, SIMS_FINDS_CLIENT);
                        String filename = websiteModel.getFilename() + ExtensionEnum.getExtension(response);
                        response.close();
                        WebsiteModel singleton = new WebsiteModel();
                        singleton.setUrl(newURL);
                        singleton.setFilename(filename);
                        singleton.setPrevious(websiteModel.getPrevious());
                        BaseEnum.print(singleton, ResponseEnum.DOWNLOAD);
                    }
                    break;
                }
            }
        } catch (Exception e) {
            BaseEnum.print(websiteModel, ResponseEnum.ERROR);
        }
    }

}
