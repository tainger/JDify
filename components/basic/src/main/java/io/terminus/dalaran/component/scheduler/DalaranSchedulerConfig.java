package io.terminus.dalaran.component.scheduler;

import lombok.Data;

@Data
public class DalaranSchedulerConfig {

    private String name;

    private Long period;

    private String cron;
}
