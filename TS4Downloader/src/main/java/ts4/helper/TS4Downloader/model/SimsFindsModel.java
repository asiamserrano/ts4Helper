package ts4.helper.TS4Downloader.model;

import lombok.Getter;
import ts4.helper.TS4Downloader.constants.SimsFindsEnum;

@Getter
public class SimsFindsModel {

    private String filename;
    private String url;
    private SimsFindsEnum simsFindsEnum;

    public SimsFindsModel(SimsFindsEnum simsFindsEnum, String url) {
        String[] strings = url.split("/");
        this.filename = strings[strings.length - 1];
        this.url = url;
        this.simsFindsEnum = simsFindsEnum;
    }

    public SimsFindsModel(SimsFindsEnum simsFindsEnum, String url, SimsFindsModel model) {
        this.filename = model.filename;
        this.url = url;
        this.simsFindsEnum = simsFindsEnum;
    }

    public boolean urlContains(String string) {
        return this.url.contains(string);
    }

}
