package org.projects.ts4.utility;

import lombok.extern.slf4j.Slf4j;
import net.lingala.zip4j.ZipFile;
import okhttp3.OkHttpClient;
import org.apache.avro.file.DataFileWriter;
import org.apache.avro.io.*;
import org.apache.avro.specific.SpecificDatumReader;
import org.apache.avro.specific.SpecificDatumWriter;
import org.projects.ts4.avro.WebsiteModel;
import org.projects.ts4.utility.classes.ScriptProcess;
import org.projects.ts4.utility.classes.Website;
import org.projects.ts4.utility.constants.ConfigConstants;
import org.projects.ts4.utility.constants.StringConstants;
import org.projects.ts4.utility.enums.ExtensionEnum;
import org.projects.ts4.utility.enums.ResponseEnum;
import org.projects.ts4.utility.utilities.FileUtility;
import org.projects.ts4.utility.utilities.OkHttpUtility;
import org.projects.ts4.utility.utilities.StringUtility;
import org.projects.ts4.utility.utilities.URLUtility;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.Objects;

@Slf4j
public class TS4UtilityApplication {

    public static void main(String[] args) {

        File directory = new File("/Users/asiaserrano/ChromeDownloads/");

        String userDir = System.getProperty(ConfigConstants.USER_DIR);
        File tarShellScript = FileUtility.createFile(userDir, "src/main/resources", "tar.sh");

        for (File file : Objects.requireNonNull(directory.listFiles())) {

//            if (!file.isDirectory() && !file.getName().startsWith(".")) {
//                ExtensionEnum extensionEnum = ExtensionEnum.valueOf(file);
//                if (extensionEnum == null) {
//                    file.delete();
//                }
//            }


            ExtensionEnum extensionEnum = ExtensionEnum.valueOf(file);
            if (extensionEnum == ExtensionEnum.RAR) {
//                unzip(file);
                untar(tarShellScript, file);
            }

        }

    }

    private static void unzip(File destination) {
        log.info("Unzipping file {}", destination);
//        try (ZipFile zipFile = new ZipFile(destination)) {
//            zipFile.extractAll(destination.getParent());
//            if (destination.delete()) {
//                log.info("file deleted: {}", destination);
//            } else {
//                log.error("unable to delete file: {}", destination);
//            }
//        } catch (Exception e) {
//            log.error("unable to unzip file {}: {}", destination, e.getMessage());
//        }
    }

    private static void untar(File tarShellScript, File destination) {
        log.info("Untarring file {}", destination);
        Process process = Objects.requireNonNull(ScriptProcess.build(tarShellScript, destination)).process;
        synchronized (process) {
            try {
                process.waitFor();
            } catch (Exception e) {
                log.error("unable to untar file {}: {}", destination, e.getMessage());
            }
        }
    }

//    public static void main(String[] args) throws Exception {
//
//        OkHttpClient client = new OkHttpClient();
//        String string = "https://shushilda.com/catalog/sims-4-cc/shoes-sims4/new-balance-ts4/";
//        URL url = URLUtility.createURLNoException(string);
//
//        String result = OkHttpUtility.getContent(url, client);
//
//        System.out.println(result);
//
//
////        List<String> links = StringUtility.loadResourceList("links.txt");
//
////        String userDir = System.getProperty(ConfigConstants.USER_DIR);
////        File file = FileUtility.createFile(userDir, "src/main/resources", "links.txt");
//
////        List<String> strings = StringUtility.loadResourceList("links.txt");
////
////        for (String string: strings) {
////            String web = string.split("  ")[1];
////
////            String url = StringUtility.getStringBetweenRegex(web, ", \"url\": \"", StringConstants.SINGLE_QUOTE);
////            System.out.println(url);
//////            System.out.println(web);
//////            System.out.println();
////        }
//
//    }

//    private static void serializePersons(List<WebsiteModel> personList, File personListSerializedFile) throws IOException {
//        DatumWriter<WebsiteModel> datumWriter = new SpecificDatumWriter<>(WebsiteModel.class);
//        DataFileWriter<WebsiteModel> dataFileWriter = new DataFileWriter<>(datumWriter);
//        dataFileWriter.create(personList.get(0).getSchema(), personListSerializedFile);
//        personList.forEach(websiteModel -> {
//            try {
//                dataFileWriter.append(websiteModel);
//            } catch (IOException e) {
//                System.err.println("Error writing website model with url: " + websiteModel.getUrl());
//            }
//        });
//        dataFileWriter.close();
//    }

//    private static WebsiteModel deserializePersons(String string) throws IOException {
//        DatumReader<WebsiteModel> reader = new SpecificDatumReader<>(WebsiteModel.class);
//
//
//
////        DataFileReader<WebsiteModel> dataFileReader = new DataStrea<>(personListSerializedFile, personDatumReader);
////        WebsiteModel person = null;
////        List<WebsiteModel> persons = new ArrayList<>();
//        // Reuse person object by passing it to next(). This saves us from allocating and garbage collecting many objects for files with many items.
//        while (dataFileReader.hasNext()) {
//            person = dataFileReader.next(person);
//            persons.add(person);
//        }
//        dataFileReader.close();
//        return persons;
//    }

}
