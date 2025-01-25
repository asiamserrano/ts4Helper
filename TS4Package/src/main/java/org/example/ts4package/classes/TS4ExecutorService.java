//package org.example.ts4package.classes;
//
//import lombok.AllArgsConstructor;
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.scheduling.annotation.EnableScheduling;
//import org.springframework.scheduling.annotation.Scheduled;
//
//import java.io.File;
//import java.time.ZonedDateTime;
//import java.time.format.DateTimeFormatter;
//import java.time.temporal.ChronoUnit;
//import java.util.concurrent.ExecutorService;
//import java.util.concurrent.Executors;
//import java.util.concurrent.ThreadPoolExecutor;
//
//@AllArgsConstructor
//@EnableScheduling
//@Slf4j
//public class TS4ExecutorService  {
//
//    public final ExecutorService executorService;
//    private final File directory;
//    private ZonedDateTime zonedDateTime;
//
//    public TS4ExecutorService(int nThreads, String directory) {
//        this.executorService = Executors.newFixedThreadPool(nThreads);
//        this.directory = new File(directory);
//        this.zonedDateTime = null;
//    }
//
//    @Scheduled(fixedDelay = 1000)
//    public void scheduleFixedRateWithInitialDelayTask() {
//        boolean active = isActive();
//        if (zonedDateTime == null) {
//            if (active) {
//                zonedDateTime = ZonedDateTime.now();
//                log.info("setting zone to {}", zonedDateTime);
//            }
//        } else {
//            if (!active) {
//                log.info("DOWNLOAD COMPLETED IN {} SECONDS", ChronoUnit.SECONDS.between(zonedDateTime, ZonedDateTime.now()));
//                zonedDateTime = null;
//            }
//        }
//    }
//
//    public File getDirectory() {
//        if (zonedDateTime == null) {
//            return this.directory;
//        } else {
//            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd_HH.mm.ss");
//            String child = "donwload_" + zonedDateTime.format(formatter);
//            return new File(this.directory, child);
//        }
//    }
//
//    public boolean isActive() {
//        int active = ((ThreadPoolExecutor) executorService).getActiveCount();
//        return active > 0;
//    }
//
//}
