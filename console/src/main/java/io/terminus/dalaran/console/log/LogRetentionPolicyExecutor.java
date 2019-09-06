package io.terminus.dalaran.console.log;

import org.springframework.scheduling.annotation.Scheduled;

public class LogRetentionPolicyExecutor {

    private static final Long HOUR_MILLISECOND = 60 * 60 * 1000L;

    private final LogRetentionPolicy logRetentionPolicy;

    public LogRetentionPolicyExecutor(LogRetentionPolicy logRetentionPolicy) {
        this.logRetentionPolicy = logRetentionPolicy;
    }

    @Scheduled(fixedDelay = 60 * 1000L, initialDelay = 60 * 1000L)
    private void execute() {
        logRetentionPolicy.processing();
    }
}
