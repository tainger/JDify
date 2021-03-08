package io.terminus.dalaran.model.dto;

import lombok.Data;

@Data
public class ElasticJobInfo extends ElasticJobConfigInfo{

    private String flowId;

    private boolean isOnline;
}
