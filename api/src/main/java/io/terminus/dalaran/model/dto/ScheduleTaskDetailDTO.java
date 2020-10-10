package io.terminus.dalaran.model.dto;

import lombok.Data;

import java.util.Date;

@Data
public class ScheduleTaskDetailDTO {

    private String taskName;

    private String cron;

    private Date fireTime;

    private Long executeTime;

    private String timeZone;
}
