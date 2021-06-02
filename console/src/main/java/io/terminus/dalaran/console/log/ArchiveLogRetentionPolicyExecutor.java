package io.terminus.dalaran.console.log;

import org.springframework.scheduling.annotation.Scheduled;

public class ArchiveLogRetentionPolicyExecutor {

    private final LogRetentionPolicy logRetentionPolicy;

    public ArchiveLogRetentionPolicyExecutor( LogRetentionPolicy logRetentionPolicy) {
        this.logRetentionPolicy = logRetentionPolicy;
    }

    @Scheduled(cron = "${terminus.dalaran.log.duration}")
    private void execute() {
        logRetentionPolicy.processing();
    }
}
