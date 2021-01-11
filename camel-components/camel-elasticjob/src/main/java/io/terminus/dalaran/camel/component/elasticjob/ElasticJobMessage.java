package io.terminus.dalaran.camel.component.elasticjob;

import org.apache.camel.Exchange;
import org.apache.camel.impl.DefaultMessage;
import org.apache.shardingsphere.elasticjob.api.ShardingContext;

import java.util.Map;

public class ElasticJobMessage extends DefaultMessage {

    private ShardingContext shardingContext;

    public ElasticJobMessage(Exchange exchange, ShardingContext shardingContext) {
        super(exchange.getContext());
        this.shardingContext = shardingContext;
        setExchange(exchange);
    }

    public ShardingContext getShardingContext() {
        return shardingContext;
    }

    @Override
    protected void populateInitialHeaders(Map<String, Object> map) {
        super.populateInitialHeaders(map);
        if (shardingContext != null) {
            map.put("jobName", shardingContext.getJobName());
            map.put("jobParameter", shardingContext.getJobParameter());
            map.put("shardingItem", shardingContext.getShardingItem());
            map.put("shardingParamete", shardingContext.getShardingParameter());
            map.put("shardingTotalCoun", shardingContext.getShardingTotalCount());
            map.put("taskId", shardingContext.getTaskId());
        }
    }

    @Override
    public DefaultMessage newInstance() {
        return new ElasticJobMessage(getExchange(), shardingContext);
    }
}
