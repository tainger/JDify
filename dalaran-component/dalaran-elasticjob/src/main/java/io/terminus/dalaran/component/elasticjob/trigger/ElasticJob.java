package io.terminus.dalaran.component.elasticjob.trigger;

import io.terminus.dalaran.core.component.DalaranTrigger;
import io.terminus.dalaran.core.component.annotation.Trigger;
import io.terminus.dalaran.core.elasticjob.ElasticJobDataSource;
import io.terminus.dalaran.core.util.UriUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.camel.model.RouteDefinition;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.HashMap;
import java.util.Map;

@Trigger(
        value = "elastic-job",
        order = 16,
        configType = ElasticJobConfig.class
)
@Slf4j
public class ElasticJob implements DalaranTrigger<ElasticJobConfig> {

    @Autowired
    private ElasticJobDataSource elasticJobDataSource;

    @Override
    public void buildFromRoute(RouteDefinition route, ElasticJobConfig config) {
        log.info("url: " + elasticJobDataSource.getUrl() + ", pass: " + elasticJobDataSource.getPassword());
        Map<String, Object> options = new HashMap<>();
        options.put("serverLists", config.getServerLists());
        options.put("namespace", config.getNamespace());
        options.put("jobName", config.getJobName());
        options.put("cron", config.getCron());
        options.put("shardingTotalCount", config.getShardingTotalCount());
        options.put("url", elasticJobDataSource.getUrl());
        options.put("username", elasticJobDataSource.getUsername());
        options.put("password", elasticJobDataSource.getPassword());
        String optionsString = UriUtils.buildOptionsQueryString(options);
        String uri = "elasticjob://" + config.getJobName() + optionsString;
        route.from(uri);
    }
}
