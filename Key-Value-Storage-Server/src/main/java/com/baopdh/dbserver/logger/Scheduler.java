package com.baopdh.dbserver.logger;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class Scheduler {
    public enum UNIT {
        HOUR, MINTUE, SECOND
    }

    private long interval;
    private int startHour, startMinute, startSecond;
    private ChronoUnit chronoUnit;
    private TimeUnit timeUnit;
    private final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);

    public Scheduler(long interval, int startHour, int startMinute, int startSecond, UNIT unit) {
        this.interval = interval;
        this.startHour = startHour;
        this.startMinute = startMinute;
        this.startSecond = startSecond;
        this.chronoUnit = getChronoUnit(unit);
        this.timeUnit = getTimeUnit(unit);
    }

    public void start(Runnable runnable) {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime nextRun = now.withHour(startHour)
                                    .withMinute(startMinute)
                                    .withSecond(startSecond);
        if (now.compareTo(nextRun) > 0)
            nextRun = nextRun.plusDays(1);

        long initialDelay = now.until(nextRun, chronoUnit);

        scheduler.scheduleAtFixedRate(runnable, initialDelay, interval, timeUnit);
    }

    private ChronoUnit getChronoUnit(UNIT unit) {
        switch(unit) {
            case HOUR:
                return ChronoUnit.HOURS;
            case MINTUE:
                return ChronoUnit.MINUTES;
            default:
                return ChronoUnit.SECONDS;
        }
    }

    private TimeUnit getTimeUnit(UNIT unit) {
        switch(unit) {
            case HOUR:
                return TimeUnit.HOURS;
            case MINTUE:
                return TimeUnit.MINUTES;
            default:
                return TimeUnit.SECONDS;
        }
    }
}
