package io.terminus.dalaran.component.trigger.scheduler;

import io.terminus.dalaran.FieldInputType;
import io.terminus.dalaran.core.component.annotation.ConfigFieldInfo;
import lombok.Data;

@Data
public class DalaranSchedulerConfig {

    @ConfigFieldInfo(label = "调度任务名", inputType = FieldInputType.String)
    private String name;

    @ConfigFieldInfo(label = "Cron 表达式", inputType = FieldInputType.String)
    private String cron;
}
