package io.terminus.dalaran.model.dto;

import io.terminus.dalaran.model.flow.FlowStatus;
import lombok.Data;

@Data
public class ElasticJobInfo extends ElasticJobConfigInfo{

    private Long flowId;

    private FlowStatus jobStatus;

    private String version;
}
