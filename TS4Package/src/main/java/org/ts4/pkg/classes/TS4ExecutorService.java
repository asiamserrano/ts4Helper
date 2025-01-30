package org.ts4.pkg.classes;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;

import java.time.ZonedDateTime;
import java.time.temporal.ChronoUnit;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;

@AllArgsConstructor
@EnableScheduling
@Slf4j
public class TS4ExecutorService  {

    public enum Action {
        CONSUME, DOWNLOAD, UNZIP
    }

    public final ExecutorService executorService;
    private final Action action;
    private ZonedDateTime startTime;

    public TS4ExecutorService(int nThreads, Action action) {
        this.executorService = Executors.newFixedThreadPool(nThreads);
        this.action = action;
        this.startTime = null;
    }

    @Scheduled(fixedDelay = 1000)
    public void scheduleFixedRateWithInitialDelayTask() {
        boolean active = ((ThreadPoolExecutor) executorService).getActiveCount() > 0;
        if (startTime == null) {
            if (active) {
                startTime = ZonedDateTime.now();
            }
        } else {
            if (!active) {
                log.info("{} COMPLETED IN {} SECONDS", this.action.toString(), ChronoUnit.SECONDS.between(startTime, ZonedDateTime.now()));
                startTime = null;
            }
        }
    }

}
