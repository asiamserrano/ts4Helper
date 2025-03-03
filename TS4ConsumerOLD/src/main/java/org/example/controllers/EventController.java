package org.example.controllers;//package ts4.helper.TS4Downloader.controllers;
//
//import lombok.AllArgsConstructor;
//import lombok.extern.slf4j.Slf4j;
//import okhttp3.OkHttpClient;
//import org.json.simple.JSONArray;
//import org.json.simple.JSONObject;
//import org.springframework.web.bind.annotation.RestController;
//import org.springframework.web.bind.annotation.RequestMapping;
//import org.springframework.web.bind.annotation.GetMapping;
//import org.springframework.web.bind.annotation.PostMapping;
//import org.springframework.web.bind.annotation.RequestParam;
//import org.springframework.web.bind.annotation.RequestBody;
//
//import java.io.File;
//import java.net.URL;
//import java.time.ZoneId;
//import java.time.ZonedDateTime;
//import java.time.format.DateTimeFormatter;
//import java.time.temporal.ChronoUnit;
//import java.util.List;
//import java.util.ArrayList;
//import java.util.Arrays;
//
//import java.util.concurrent.ExecutorService;
//import java.util.concurrent.Executors;
//import java.util.concurrent.Callable;
//
//import ts4.helper.TS4Downloader.threads.URLModelThread;
//import org.example.ts4package.enums.ResponseEnum;
//import org.example.ts4package.models.DownloadResponse;
//import org.example.ts4package.models.URLModel;
//import org.example.ts4package.utilities.ConsolidateUtility;
//import org.example.ts4package.utilities.FileUtility;
//
//import static org.example.ts4package.constants.ControllerConstants.*;
//
//import static org.example.ts4package.constants.StringConstants.*;
//import static org.example.ts4package.enums.ResponseEnum.SUCCESSFUL;
//
//@RestController
//@RequestMapping(EVENT_CONTROLLER_REQUEST_MAPPING)
//@Slf4j
//@AllArgsConstructor
//public class EventController {
//
//    private File nonDownloadedLinksFile;
//    private OkHttpClient client;
//
//    private static JSONObject MAP;
//
//    @GetMapping(EVENT_CONTROLLER_SAMPLE_GET_MAPPING)
//    public String sample() {
//        ZonedDateTime zdt = ZonedDateTime.now(ZoneId.of("America/New_York"));
//        String value = String.format("sample endpoint was hit at %s", zdt);
//        log.info(value);
//        return value;
//    }
//
//    @PostMapping(EVENT_CONTROLLER_CONSOLIDATE_POST_MAPPING)
//    public void consolidate(@RequestParam String location) {
//        File file = new File(location);
//        ConsolidateUtility.consolidate(file);
//    }
//
//    @PostMapping(EVENT_CONTROLLER_DOWNLOAD_LINKS_POST_MAPPING)
//    public String downloadLinks(@RequestParam String location, @RequestBody URL[] body) {
//        MAP = new JSONObject();
//        List<URLModel> urlModels = Arrays.stream(body).map(s -> new URLModel(s, EMPTY)).toList();
//        ZonedDateTime START = ZonedDateTime.now();
//        File directory = new File(location, "download_" + START.format(DATE_TIME_FORMATTER));
//        if (FileUtility.createDirectory(directory)) {
//            String response = downloadLinks(directory, urlModels, 1);
//            ZonedDateTime END = ZonedDateTime.now();
//            log.info("DOWNLOAD COMPLETED IN {} SECONDS", ChronoUnit.SECONDS.between(START, END));
//            File summary = new File(directory, "_summary.json");
//            FileUtility.writeToFile(summary, MAP, false);
//            return "done";
//        } else {
//            log.error("unable to create directory {}", directory.getAbsolutePath());
//            return MAP.toJSONString();
//        }
//    }
//
//    @SuppressWarnings(UNCHECKED)
//    private String downloadLinks(File directory, List<URLModel> urlModels, int iteration) {
//        if (urlModels.isEmpty()) {
//            return MAP.toJSONString();
//        } else {
//            int size = urlModels.size();
//            log.info("iteration: {} | # of urls: {}", iteration, size);
//            List <URLModel> newURLs = new ArrayList<>();
//            try(ExecutorService executor = Executors.newFixedThreadPool(size)) {
//                urlModels.parallelStream().forEach(urlModel -> {
//                    try {
//                        Callable<DownloadResponse> worker = new URLModelThread(urlModel, client, directory);
//                        DownloadResponse response = executor.submit(worker).get();
//                        ResponseEnum responseEnum = response.responseEnum;
//                        if (responseEnum == SUCCESSFUL) {
//                            newURLs.addAll(response.models);
//                        } else {
//                            JSONArray array = (JSONArray) MAP.getOrDefault(responseEnum, new JSONArray());
//                            JSONObject jsonObject = response.model.toJSON();
//                            array.add(jsonObject);
//                            MAP.put(responseEnum, array);
//                        }
//                    } catch (Exception e) {
//                        log.error("unable to get future for url: {}", urlModel.url, e);
//                    }
//                });
//                executor.shutdown();
//                while (!executor.isTerminated()) {}
//            } catch (Exception e) {
//                log.error("error when running parsing urlModels", e);
//            }
//            return downloadLinks(directory, newURLs, iteration + 1);
//        }
//    }
//
//}