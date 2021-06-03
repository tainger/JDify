package io.terminus.dalaran.console.log;

import org.springframework.scheduling.annotation.Scheduled;

public class ArchiveLogRetentionPolicyExecutor {

    private static final long HOUR_MILLISECOND = 60 * 60 * 1000L;

    private static final String LOG_CRON = "0 0 10 */15,25,26,27,28,29,30 * ?";

    private final LogRetentionPolicy logRetentionPolicy;

    public ArchiveLogRetentionPolicyExecutor( LogRetentionPolicy logRetentionPolicy) {
        this.logRetentionPolicy = logRetentionPolicy;
    }

    @Scheduled(cron = "${terminus.dalaran.log.duration}")
    private void execute() {
        logRetentionPolicy.processing();
    }
}
