package org.example.ts4package;

import com.google.common.io.Resources;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.errors.SerializationException;
import org.example.MyAvroClass;
import org.example.WebsiteModel;
import org.example.ts4package.utilities.KafkaUtility;
import org.springframework.kafka.core.KafkaTemplate;

import java.io.File;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;

class Playground {

    private static final String BOOTSTRAP_SERVERS = "localhost:9092";

    private static final KafkaTemplate<String, String> KAFKA_TEMPLATE =
            KafkaUtility.createKafkaTemplate(BOOTSTRAP_SERVERS);

    public static void main(String[] args) throws Exception {

//        MyAvroClass myAvroClass = new MyAvroClass("Asia", 13, "red", null);

        WebsiteModel websiteModel = new WebsiteModel();
        websiteModel.setUrl("https://www.google.com");
        websiteModel.setDirectory("/Users/asia/zzz");
        websiteModel.setFilename("myfile.txt");

        KafkaProducer<String, WebsiteModel> kafkaProducer = KafkaUtility.createKafkaProducer(BOOTSTRAP_SERVERS);
        ProducerRecord<String, WebsiteModel> record =
                new ProducerRecord<>("website-model-topic", "website-model-key", websiteModel);
        try {
            kafkaProducer.send(record);
        } catch(SerializationException e) {
            System.out.println("cannot serialize record");
        }
        finally {
            kafkaProducer.flush();
            kafkaProducer.close();
            System.out.println("sent record");
        }

//        File personsListSerializedFile = new File("my_avros.avro");
//        AvroSerializerUtility.serializeMyAvros(myAvros, personsListSerializedFile);
//        System.out.println("done serializing");
//        AvroDeserializerUtility.deserializeMyAvros(personsListSerializedFile).stream().map(MyAvroClass::toString)
//                .forEach(System.out::println);
//        System.out.println("done deserializing");
//
//        KafkaProducer kafkaProducer = KafkaUtility.createKafkaProducer(BOOTSTRAP_SERVERS);
//        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd_HH.mm.ss");
//        String child = ZonedDateTime.now().format(formatter);
//        File file = new File("/Users/asia/zzz", child);
//        if (file.exists() || file.mkdir()) {
//            URL contentURL = Resources.getResource("input.txt");
//            Files.readAllLines(Paths.get(contentURL.toURI()), StandardCharsets.UTF_8).parallelStream()
//                    .map(str -> {
//                        try {
//                            WebsiteModel websiteModel = new WebsiteModel();
//                            websiteModel.setUrl(str);
//                            websiteModel.setDirectory(file.getAbsolutePath());
//                            return websiteModel;
//                        } catch (Exception e) {
//                            System.out.println("unable to create model " + e.getMessage());
//                            throw new RuntimeException(e);
//                        }
//                    })
////                    .forEach(str -> System.out.println(str.toString()));
//                .forEach(websiteModel -> {
//                            try {
//                                ProducerRecord<Object, Object> record =
//                                        new ProducerRecord<>("my-avro-topic", "record-key", websiteModel);
//                                kafkaProducer.send(record);
//                            } catch (SerializationException e) {
//                                System.out.println("cannot serialize record");
//                            } finally {
//                                System.out.println("sent record");
//                            }
//
//                    kafkaProducer.flush();
//                    kafkaProducer.close();
//                });
//        }


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
