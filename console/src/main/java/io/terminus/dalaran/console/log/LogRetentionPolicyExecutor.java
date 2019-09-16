package io.terminus.dalaran.console.log;

import org.springframework.scheduling.annotation.Scheduled;

public class LogRetentionPolicyExecutor {

    private static final long HOUR_MILLISECOND = 60 * 60 * 1000L;

    private final LogRetentionPolicy logRetentionPolicy;

    public LogRetentionPolicyExecutor(LogRetentionPolicy logRetentionPolicy) {
        this.logRetentionPolicy = logRetentionPolicy;
    }

    @Scheduled(fixedDelay = HOUR_MILLISECOND, initialDelay = HOUR_MILLISECOND)
    private void execute() {
        logRetentionPolicy.processing();
    }
}
