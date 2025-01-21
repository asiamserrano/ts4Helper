package ts4.helper.TS4Downloader;

import okhttp3.OkHttpClient;
import okhttp3.Response;
import ts4.helper.TS4Downloader.utilities.FileUtility;
import ts4.helper.TS4Downloader.utilities.OkHttpUtility;
import ts4.helper.TS4Downloader.utilities.URLUtility;

import java.io.File;
import java.net.URL;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;

public class Tester {

    public static void main(String[] args) {
        ZonedDateTime START = ZonedDateTime.now();
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd_HH.mm.ss");
        File file = new File("/Users/asia/zzz", "download_" + START.format(dtf));
        if (FileUtility.createDirectory(file)) {
            System.out.println("created");
        } else {
            System.out.println("not created");
        }
    }

}
