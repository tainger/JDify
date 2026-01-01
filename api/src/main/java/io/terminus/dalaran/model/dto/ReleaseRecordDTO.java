package io.terminus.dalaran.model.dto;

import lombok.Data;

import java.util.Date;

@Data
public class ReleaseRecordDTO {

    ThreadLocal
    private Long id;

    private String version;

    private boolean successful;

    private boolean enabled;

    private String releaseLog;

    private Long operator;

    private Date releaseTime;
}
