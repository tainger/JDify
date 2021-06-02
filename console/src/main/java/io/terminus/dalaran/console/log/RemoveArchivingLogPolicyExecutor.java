package io.terminus.dalaran.console.log;

import org.springframework.scheduling.annotation.Scheduled;

public class RemoveArchivingLogPolicyExecutor {

    private static final String LOG_CRON = "0 0 1 * * ?";



    private final RemoveArchivingLogPolicy removeArchivingLogPolicy;

    public RemoveArchivingLogPolicyExecutor(RemoveArchivingLogPolicy removeArchivingLogPolicy) {
        this.removeArchivingLogPolicy = removeArchivingLogPolicy;
    }

    @Scheduled(cron = LOG_CRON)
    private void execute() {
        removeArchivingLogPolicy.processing();
    }
}
