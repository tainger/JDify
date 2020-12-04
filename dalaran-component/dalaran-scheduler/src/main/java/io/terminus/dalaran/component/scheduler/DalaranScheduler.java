package io.terminus.dalaran.component.scheduler;

import io.terminus.dalaran.core.component.DalaranTrigger;
import io.terminus.dalaran.core.component.annotation.Trigger;
import io.terminus.dalaran.core.util.UriUtils;
import org.apache.camel.model.RouteDefinition;

import java.util.HashMap;
import java.util.Map;

@Trigger(
        value = "scheduler",
        order = 16,
        configType = DalaranSchedulerConfig.class
)
public class DalaranScheduler implements DalaranTrigger<DalaranSchedulerConfig> {
    @Override
    public void buildFromRoute(RouteDefinition route, DalaranSchedulerConfig config) {
        Map<String, Object> options = new HashMap<>();
        options.put("cron", config.getCron());
        options.put("stateful", config.getStateful());
        options.put("deleteJob", config.getDeleteJob());
        options.put("pauseJob", config.getPauseJob());
        options.put("trigger.timeZone", config.getTimezone());
        options.put("trigger.groupName", "Dalaran-Scheduler");
        options.put("trigger.triggerName", config.getTaskName());
        String optionsString = UriUtils.buildOptionsQueryString(options);
        String uri = "quartz2://" + config.getTaskName() + optionsString;
        route.from(uri);
    }
}
