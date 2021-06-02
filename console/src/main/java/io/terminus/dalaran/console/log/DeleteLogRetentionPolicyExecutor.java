package io.terminus.dalaran.console.log;

import org.springframework.scheduling.annotation.Scheduled;

public class DeleteLogRetentionPolicyExecutor {

    private DeleteLogRetentionPolicy deleteLogRetentionPolicy;

    private static final String LOG_CRON = "0 0 1 * * ?";

    public DeleteLogRetentionPolicyExecutor(DeleteLogRetentionPolicy deleteLogRetentionPolicy) {
        this.deleteLogRetentionPolicy = deleteLogRetentionPolicy;
    }


    @Scheduled(cron = LOG_CRON)
    private void execute() {
        deleteLogRetentionPolicy.processing();
    }





}
