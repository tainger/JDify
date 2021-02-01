package io.terminus.dalaran.model.dto.log;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.util.Date;

@Data
public class ElasticJobLogDTO {

    private String id;

    private String jobName;

    private String taskId;

    private String hostname;

    private String ip;

    private Integer shardingItem;

    private String executionSource;

    private String failureCause;

    private Boolean isSuccess;

    @JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd hh:mm:ss")
    private Date startTime;

    @JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd hh:mm:ss")
    private Date completeTime;

}
