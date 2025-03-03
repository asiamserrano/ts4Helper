package org.projects.ts4.utility.classes;

import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;

import java.time.ZonedDateTime;
import java.time.temporal.ChronoUnit;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.ThreadPoolExecutor;

@Slf4j
@EnableScheduling
public abstract class Scheduler {

    protected final ExecutorService executorService;
    private ZonedDateTime startTime;

    public Scheduler(final ExecutorService executorService) {
        this.executorService = executorService;
    }

    @Scheduled(cron = "0/1 * * * * *")
    private void start() {
        if (isNull() && isActive()) {
            this.startTime = ZonedDateTime.now();
        }
    }

    @Scheduled(cron = "0/1 * * * * *")
    private void end() {
        if (isNotNull() && isInctive()) {
            ZonedDateTime endTime = ZonedDateTime.now();
            long minutes = ChronoUnit.MINUTES.between(startTime, endTime);
            if (minutes > 2) {
                long seconds = minutes % 60;
                log.info("done in {} minutes {} seconds", minutes, seconds);
            } else {
                long seconds = ChronoUnit.SECONDS.between(startTime, endTime);
                log.info("done in {} seconds", seconds);
            }
            this.startTime = null;
        }
    }

    private boolean isNull() {
        return this.startTime == null;
    }

    private boolean isNotNull() {
        return this.startTime != null;
    }

    private boolean isActive() {
        return getActiveCount() > 0;
    }

    private boolean isInctive() {
        return getActiveCount() == 0;
    }

    private int getActiveCount() {
        if (executorService instanceof ThreadPoolExecutor threadPoolExecutor) {
            return threadPoolExecutor.getActiveCount();
        } else {
            log.error("ExecutorService is not an instance of ThreadPoolExecutor");
            return -1;
        }
    }

}
