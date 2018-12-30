package org.sample.concurrency;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class StorageAndStatisticsCalculator {
    private final long delayInSecondsToExpire;
    private Snapshot snapshot = new Snapshot();
    private static ScheduledExecutorService scheduledExecutorService = Executors.newSingleThreadScheduledExecutor();

    public StorageAndStatisticsCalculator(){
        this(60);
    }

    public StorageAndStatisticsCalculator(long delayInSecondsToExpire){
        this.delayInSecondsToExpire = delayInSecondsToExpire;
    }

    public synchronized void add(Transaction request) {
        snapshot.add(request);
        scheduledExecutorService.schedule(() -> snapshot.expire(request), delayInSecondsToExpire, TimeUnit.SECONDS);
    }

    public synchronized Statistics snapshot() {
        return snapshot.x();
    }

    public synchronized void clear() {
        snapshot.clear();
    }
}
