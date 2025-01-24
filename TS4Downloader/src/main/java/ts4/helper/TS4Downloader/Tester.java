package ts4.helper.TS4Downloader;

import okhttp3.OkHttpClient;
import okhttp3.Response;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;
import ts4.helper.TS4Downloader.models.URLModel;
import ts4.helper.TS4Downloader.models.WebsiteModel;
import ts4.helper.TS4Downloader.utilities.FileUtility;
import ts4.helper.TS4Downloader.utilities.OkHttpUtility;
import ts4.helper.TS4Downloader.utilities.StringUtility;
import ts4.helper.TS4Downloader.utilities.URLUtility;

import java.io.File;
import java.net.URL;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.UUID;
import java.util.regex.Matcher;

//import org.example.Main;
//import org.example.

import org.example.ts4package.Main;
import org.example.ts4package.SecondMain;

public class Tester {

    public static void main(String[] args) {
        UUID uuid = Main.getRandomUUID();
        System.out.println(uuid);
        uuid = SecondMain.getRandomUUID();
        System.out.println(uuid);
//        UUID uuid = Main.getRandomUUID();
//        System.out.println(uuid);

//        String string = "{\"previous\":\"{\"previous\":null,\"name\":\"SATIN SNAKE-COIL SANDALS | Patreon\",\"url\":\"https://www.patreon.com/posts/56379158\"}\",\"name\":\"RUCHELLSIMS_ SatinSnake-CoilSandals_NewMesh_bySayumiRuchell.package\",\"url\":\"https://www.patreon.com/file?h=56379158&i=8917573\"}"
//            ;
//
//        String string1 = "{\"previous\":null,\"name\":\"SATIN SNAKE-COIL SANDALS | Patreon\",\"url\":\"https://www.patreon.com/posts/56379158\"}";
    }

}
