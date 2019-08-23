package io.terminus.dalaran.component.trigger.scheduler;

import io.terminus.dalaran.core.component.FieldInputType;
import io.terminus.dalaran.core.component.annotation.ConfigFieldInfo;
import lombok.Data;

@Data
public class DalaranSchedulerConfig {

    @ConfigFieldInfo(label = "Cron 表达式", inputType = FieldInputType.String)
    private String cron;
}
