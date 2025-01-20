package ts4.helper.TS4Downloader;

import okhttp3.OkHttpClient;
import okhttp3.Response;
import ts4.helper.TS4Downloader.utilities.OkHttpUtility;
import ts4.helper.TS4Downloader.utilities.URLUtility;

import java.net.URL;

public class Tester {

    public static void main(String[] args) {
        String str = "https://click.simsfinds.com/download?flid=173321671320709&pass=2307102333&version=1733216713&key=9e4a77dd0c64425f1da52a38303d83e3&cid=316599";
        URL url = URLUtility.createURL(str);
        Response response = OkHttpUtility.sendRequest(url, new OkHttpClient());
        String info = response.header("Content-Type");
        response.close();

        System.out.println(info);
    }
}
