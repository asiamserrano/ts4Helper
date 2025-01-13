package ts4.helper.TS4Downloader.models;

import ts4.helper.TS4Downloader.enums.SimsFindsEnum;

import java.net.URL;

import static ts4.helper.TS4Downloader.constants.StringConstants.FORWARD_SLASH;

public class SimsFindsModel {

    public final String filename;
//    public final String url;
    public final URL url;
    public final SimsFindsEnum simsFindsEnum;

    public SimsFindsModel(SimsFindsEnum simsFindsEnum, URL url) {
        String[] strings = url.toString().split(FORWARD_SLASH);
        this.filename = strings[strings.length - 1];
        this.url = url;
        this.simsFindsEnum = simsFindsEnum;
    }

    public SimsFindsModel(SimsFindsEnum simsFindsEnum, URL url, SimsFindsModel model) {
        this.filename = model.filename;
        this.url = url;
        this.simsFindsEnum = simsFindsEnum;
    }

//    public SimsFindsModel(SimsFindsEnum simsFindsEnum, String url) {
//        String[] strings = url.split(FORWARD_SLASH);
//        this.filename = strings[strings.length - 1];
//        this.url = url;
//        this.simsFindsEnum = simsFindsEnum;
//    }
//
//    public SimsFindsModel(SimsFindsEnum simsFindsEnum, String url, SimsFindsModel model) {
//        this.filename = model.filename;
//        this.url = url;
//        this.simsFindsEnum = simsFindsEnum;
//    }

    public boolean urlContains(String string) {
        return this.url.toString().contains(string);
    }

}
