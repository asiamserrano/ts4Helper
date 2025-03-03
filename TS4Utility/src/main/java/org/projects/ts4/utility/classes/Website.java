package org.projects.ts4.utility.classes;

import org.projects.ts4.avro.WebsiteModel;
import org.projects.ts4.utility.utilities.FileUtility;
import org.projects.ts4.utility.utilities.URLUtility;

import java.io.File;
import java.net.URL;
import java.util.UUID;

public class Website {

    public static WebsiteModel build(String url, File dir) {
        return build(url, dir, null);
    }

    public static WebsiteModel build(String url, File dir, WebsiteModel previous) {
        return new Website()
                .setURL(url)
                .setDirectory(dir)
                .setPrevious(previous)
                .build();
    }

    public static WebsiteModel build(String url, String name, WebsiteModel websiteModel) {
        return build(url, name, websiteModel, true);
    }

    public static WebsiteModel build(String url, String name, WebsiteModel websiteModel, boolean previous) {
        File dir = FileUtility.createDirectory(websiteModel.getDirectory());
        WebsiteModel prev = previous ? websiteModel : websiteModel.getPrevious();
        return new Website()
                .setURL(url)
                .setDirectory(dir)
                .setName(name)
                .setPrevious(prev)
                .build();
    }

//    public static WebsiteModel build(String url, String name, WebsiteModel websiteModel) {
//        return new Website()
//                .setURL(url)
//                .setFile(websiteModel, name)
//                .setPrevious(websiteModel)
//                .build();
//    }

    private final UUID uuid;
    private URL url;
//    private File file;
    private File directory;
    private String name;
//    private File filename;
    private WebsiteModel previous;
    
    public Website() {
        this.uuid = UUID.randomUUID();
        this.url = null;
//        this.file = null;
        this.directory = null;
        this.name = null;
        this.previous = null;
    }

    private Website setURL(String string) {
        URL url = URLUtility.createURLNoException(string);
        return setURL(url);
    }

    private Website setURL(URL url) {
        this.url = url;
        return this;
    }

//    private Website setFile(WebsiteModel model, String name) {
//        File directory = FileUtility.createDirectory(model.getDirectory());
//        return this.setDirectory(directory).setName(name);
//    }

    private Website setDirectory(File directory) {
        this.directory = directory;
        return this;
    }

    private Website setName(String name) {
        this.name = name;
        return this;
    }

    private Website setPrevious(WebsiteModel previous) {
        this.previous = previous;
        return this;
    }

    private WebsiteModel build() {
        WebsiteModel websiteModel = new WebsiteModel();
        websiteModel.setUuid(this.uuid.toString());
        websiteModel.setUrl(this.url.toString());
        websiteModel.setDirectory(this.directory.toString());
        websiteModel.setFilename(this.name);
        websiteModel.setPrevious(this.previous);
        return websiteModel;
    }
    
}
