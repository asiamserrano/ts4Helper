package ts4.helper.TS4Downloader.downloaders;

import ts4.helper.TS4Downloader.models.DownloadResponse;

import java.io.File;
import java.net.URL;

public interface Downloader {
    DownloadResponse download(URL url, File starting_directory) throws Exception;
}
