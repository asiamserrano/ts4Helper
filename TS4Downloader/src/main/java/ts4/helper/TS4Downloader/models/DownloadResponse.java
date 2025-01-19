package ts4.helper.TS4Downloader.models;

import lombok.AllArgsConstructor;
import ts4.helper.TS4Downloader.enums.ResponseEnum;

import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static ts4.helper.TS4Downloader.enums.ResponseEnum.SUCCESSFUL;
import static ts4.helper.TS4Downloader.enums.ResponseEnum.FAILURE;

@AllArgsConstructor
public class DownloadResponse {

    public final ResponseEnum responseEnum;
    public final URLModel model;
    public final List<URLModel> models;

    public DownloadResponse(ResponseEnum responseEnum, URLModel model) {
        this.responseEnum = responseEnum;
        this.model = model;
        this.models = new ArrayList<>();
    }

    public DownloadResponse(URLModel model, List<URLModel> models) {
        this.responseEnum = models.isEmpty() ? FAILURE : SUCCESSFUL;
        this.model = model;
        this.models = models;
    }

    public DownloadResponse(ResponseEnum responseEnum, URLModel model, URLModel singleton) {
        this.responseEnum = responseEnum;
        this.model = model;
        this.models = new ArrayList<>(Collections.singleton(singleton));
    }

}