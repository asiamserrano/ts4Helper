//package org.projects.ts4.utility.classes;
//
//import lombok.extern.slf4j.Slf4j;
//import org.projects.ts4.utility.enums.KafkaTopicEnum;
//import org.springframework.scheduling.annotation.EnableScheduling;
//import org.springframework.scheduling.annotation.Scheduled;
//
//import java.time.ZonedDateTime;
//import java.time.temporal.ChronoUnit;
//import java.util.concurrent.ExecutorService;
//import java.util.concurrent.ThreadPoolExecutor;
//
//@Slf4j
//@EnableScheduling
//public abstract class Scheduler {
//
//    protected final ExecutorService executorService;
//    private final String action;
//    private ZonedDateTime zonedDateTime;
//
//    protected Scheduler(ExecutorService executorService, KafkaTopicEnum kafkaTopicEnum) {
//        this.executorService = executorService;
//        this.action = kafkaTopicEnum == KafkaTopicEnum.CONSUMER ? "CONSUME" : "DOWNLOAD";
//        this.zonedDateTime = null;
//    }
//
//    protected boolean isZoneDateTimeNull() {
//        return zonedDateTime == null;
//    }
//
//    protected boolean isZoneDateTimeValid() {
//        return zonedDateTime != null;
//    }
//
//    protected boolean isQueueEmpty() {
//        return getQueueSize() == 0;
//    }
//
//    protected boolean isQueueActive() {
//        return getQueueSize() > 0;
//    }
//
//    protected void setZonedDateTime(ZonedDateTime zdt) {
//        if (zdt == null) {
//            long seconds = ChronoUnit.SECONDS.between(zonedDateTime, ZonedDateTime.now());
//            log.info("{} COMPLETED IN {} SECONDS", action, seconds);
//            this.zonedDateTime = null;
//        } else {
//            log.info("setting zonedDateTime to {}", zdt);
//            this.zonedDateTime = zdt;
//        }
//    }
//
//    @Scheduled(cron = "0/5 * * * * ?")
//    private void isInactive() {
//        if (isZoneDateTimeNull()) log.info("INACTIVE");
//    }
//
//    private int getQueueSize() {
//        if (executorService instanceof ThreadPoolExecutor threadPoolExecutor) {
//            return threadPoolExecutor.getQueue().size();
//        } else {
//            log.error("unable to get ThreadPoolExecutor: {}", executorService);
//            return -1;
//        }
//    }
//
//    public void execute(Runnable runnable) {
//        executorService.execute(runnable);
//    }
//
//}
