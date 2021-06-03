package io.terminus.dalaran.model.dto;

import lombok.Data;

@Data
public class ElasticJobTaskDetailDTO {

    private String taskName;

    private String cron;

    private String fireTime;

    private Long executeTime;
}
