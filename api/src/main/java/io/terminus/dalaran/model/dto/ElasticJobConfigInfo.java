package io.terminus.dalaran.model.dto;

import lombok.Data;

@Data
public class ElasticJobConfigInfo {

    private String serverLists;

    private String namespace;

    private String jobName;

    private String cron;

    private Integer shardingTotalCount;
}
