package com.ipatrikeev.runtime;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.util.concurrent.TimeUnit;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class ExecutionTimer {
    private final long start = System.nanoTime();
    
    public static ExecutionTimer measure() {
        return new ExecutionTimer();
    }
    
    public long executionTime(TimeUnit timeUnit) {
        return timeUnit.convert(getTimePassedNanos(), TimeUnit.NANOSECONDS);
    }
    
    public String readableExecutionTime() {
        long timePassed = getTimePassedNanos();
        long seconds = TimeUnit.NANOSECONDS.toSeconds(timePassed);
        long millis = TimeUnit.NANOSECONDS.toMillis(timePassed) - TimeUnit.SECONDS.toMillis(seconds);
        return String.format("%ds %dms.", seconds, millis);
    }

    private long getTimePassedNanos() {
        return System.nanoTime() - start;
    }
}
