package io.terminus.dalaran.console.log;

import org.springframework.scheduling.annotation.Scheduled;

public class LogRetentionPolicyExecutor {

    private static final long HOUR_MILLISECOND = 60 * 60 * 1000L;

    private static final String LOG_CRON = "0 0 1 * * ?";

    private final LogRetentionPolicy logRetentionPolicy;

    public LogRetentionPolicyExecutor(LogRetentionPolicy logRetentionPolicy) {
        this.logRetentionPolicy = logRetentionPolicy;
    }

    @Scheduled(cron = LOG_CRON)
    private void execute() {
        logRetentionPolicy.processing();
    }
}
