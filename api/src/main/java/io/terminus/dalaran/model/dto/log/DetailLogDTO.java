package io.terminus.dalaran.model.dto.log;

import lombok.Data;

@Data
public class DetailLogDTO {

    private String name;

    private String triggerType;

    private String version;

    private String module;

    private boolean online;

    private String description;

    private boolean alert;

    private Double avgTime;

    private Long maxTime;

    private String lastExceptionDate;

}
