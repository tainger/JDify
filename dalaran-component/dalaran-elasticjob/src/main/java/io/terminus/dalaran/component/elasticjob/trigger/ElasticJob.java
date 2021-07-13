package io.terminus.dalaran.component.elasticjob.trigger;

import io.terminus.dalaran.DalaranConstants;
import io.terminus.dalaran.core.component.DalaranTrigger;
import io.terminus.dalaran.core.component.annotation.Trigger;
import io.terminus.dalaran.core.util.UriUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.camel.model.RouteDefinition;

import java.util.HashMap;
import java.util.Map;

@Trigger(
        value = "elastic-job",
        order = 16,
        configType = ElasticJobConfig.class,
        developer = DalaranConstants.DALARAN
)
@Slf4j
public class ElasticJob implements DalaranTrigger<ElasticJobConfig> {


    @Override
    public void buildFromRoute(RouteDefinition route, ElasticJobConfig config) {
        Map<String, Object> options = new HashMap<>();
        options.put("serverLists", config.getServerLists());
        options.put("namespace", config.getNamespace());
        options.put("jobName", config.getJobName());
        options.put("cron", config.getCron());
        options.put("shardingTotalCount", config.getShardingTotalCount());
        String optionsString = UriUtils.buildOptionsQueryString(options);
        String uri = "elasticjob://" + config.getJobName() + optionsString;
        route.from(uri);
    }
}
