package io.terminus.dalaran.model.dto;

import io.terminus.dalaran.model.flow.FlowStatus;
import lombok.Data;

@Data
public class ElasticJobInfo {

    private Long flowId;

    private FlowStatus jobStatus;

    private String serverLists;

    private String namespace;

    private String jobName;

    private String cron;

    private Integer shardingTotalCount;
}
