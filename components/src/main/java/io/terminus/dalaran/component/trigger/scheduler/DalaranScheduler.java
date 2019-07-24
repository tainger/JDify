package io.terminus.dalaran.component.trigger.scheduler;

import io.terminus.dalaran.core.component.DalaranTrigger;
import io.terminus.dalaran.core.component.annotation.Trigger;
import io.terminus.dalaran.core.util.UriUtils;
import org.apache.camel.model.RouteDefinition;

import java.util.HashMap;
import java.util.Map;

@Trigger(
        value = "scheduler",
        name = "调度触发器",
        isVoid = true,
        configType = DalaranSchedulerConfig.class
)
public class DalaranScheduler implements DalaranTrigger<DalaranSchedulerConfig> {
    @Override
    public void buildFromRoute(RouteDefinition route, DalaranSchedulerConfig config) {
        Map<String, Object> options = new HashMap<>();
        options.put("cron", config.getCron());
        String optionsString = UriUtils.buildOptionsQueryString(options);
        String uri = "quartz2://" + config.getName() + optionsString;
        route.from(uri);
    }
}
