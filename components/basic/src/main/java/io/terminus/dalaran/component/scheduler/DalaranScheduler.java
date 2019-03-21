package io.terminus.dalaran.component.scheduler;

import io.terminus.dalaran.DalaranTrigger;
import io.terminus.dalaran.annotation.DalaranComponent;
import io.terminus.dalaran.util.UriUtils;

import java.util.HashMap;
import java.util.Map;

@DalaranComponent(value = "scheduler", configType = DalaranSchedulerConfig.class)
public class DalaranScheduler implements DalaranTrigger<DalaranSchedulerConfig> {
    @Override
    public String buildRouterUri(DalaranSchedulerConfig config) {
        Map<String, Object> options = new HashMap<>();
        options.put("cron", config.getCron());
        String optionsString = UriUtils.buildOptionsQueryString(options);
        StringBuffer uri = new StringBuffer("quartz2://");
        uri.append(config.getName());
        uri.append(optionsString);
        return uri.toString();
    }
}
