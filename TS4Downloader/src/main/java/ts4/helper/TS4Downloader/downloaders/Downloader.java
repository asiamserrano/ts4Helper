package ts4.helper.TS4Downloader.downloaders;

public interface Downloader {

    boolean download(String content, String location) throws Exception;

}
