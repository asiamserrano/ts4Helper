package org.example.ts4package;

import com.google.common.io.Resources;
import org.example.ts4package.models.MessageModel;
import org.example.ts4package.models.WebsiteModel;
import org.example.ts4package.utilities.KafkaUtility;
import org.springframework.kafka.core.KafkaTemplate;

import java.io.File;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;

import static org.example.ts4package.enums.TopicEnum.MY_TOPIC;

class Playground {

    private static final String BOOTSTRAP_SERVERS = "localhost:9092";

    private static final KafkaTemplate<String, String> KAFKA_TEMPLATE =
            KafkaUtility.createKafkaTemplate(BOOTSTRAP_SERVERS);

    public static void main(String[] args) throws Exception {

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd_HH.mm.ss");
        String child = "donwload_" + ZonedDateTime.now().format(formatter);
        File file = new File("/Users/asiaserrano/ChromeDownloads", child);
        if (file.exists() || file.mkdir()) {
            URL contentURL = Resources.getResource("input.txt");
            Files.readAllLines(Paths.get(contentURL.toURI()), StandardCharsets.UTF_8).parallelStream()
                    .map(str -> {
                        try {
                            WebsiteModel wm = new WebsiteModel.Builder(str).build();
                            return new MessageModel.Builder(file, wm).build();
                        } catch (Exception e) {
                            System.out.println("unable to create website model " + e.getMessage());
                            throw new RuntimeException(e);
                        }
                    })
//                    .forEach(str -> System.out.println(str.toString()));
                .forEach(str -> KafkaUtility.send(str, MY_TOPIC, KAFKA_TEMPLATE));
        }


//        String message = "{\"previous\":{\"previous\":null,\"name\":\"Google\",\"url\":\"https://www.google.com\"},\"name\":\"MSN\",\"url\":\"https://www.msn.com\"}";
//        WebsiteModel.Builder wm = WebsiteModel.Builder.parse(message);
//
//        String dir = "/Users/asiaserrano/ChromeDownloads";
//        ZonedDateTime now = ZonedDateTime.now();
//        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd_HH.mm.ss");
//        File directory = new File(dir, "download_" + formatter.format(now));
//
//        MessageModel model = new MessageModel.Builder(directory.getAbsolutePath(), wm).build();
//
//        System.out.println(model);
//        System.out.println(directory.getAbsolutePath());

//        String message = "{\"previous\":{\"previous\":null,\"name\":\"Google\",\"url\":\"https://www.google.com\"},\"name\":\"MSN\",\"url\":\"https://www.msn.com\"}";
//        WebsiteModel wm = WebsiteModel.Builder.parse(message).build();
//        System.out.println(wm);
//
//        String message2 = "{\"previous\":null,\"name\":\"Google\",\"url\":\"https://www.google.com\"}";
//        WebsiteModel wm2 = WebsiteModel.Builder.parse(message2).build();
//        System.out.println(wm2);
    }

}
