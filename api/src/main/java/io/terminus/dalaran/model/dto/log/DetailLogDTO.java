package io.terminus.dalaran.model.dto.log;

import lombok.Data;

@Data
public class DetailLogDTO {

    private String name;

    private String triggerType;

    private String version;

    private String moduleName;

    private boolean online;

    private boolean isMonitor;

    private String description;

    private Double avgTime;

    private Long maxTime;

    private String maxTimeRecordId;

    private String lastExceptionDate;

    private String lastExceptionDateRecordId;

}
