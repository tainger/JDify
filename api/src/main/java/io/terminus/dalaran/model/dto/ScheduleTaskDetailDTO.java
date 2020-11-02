package io.terminus.dalaran.model.dto;

import lombok.Data;

@Data
public class ScheduleTaskDetailDTO {

    private String taskName;

    private String cron;

    private String fireTime;

    private Long executeTime;

    private String timeZone;
}
