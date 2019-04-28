package io.terminus.dalaran.console.model.dto;

import lombok.Data;

import java.util.Date;

@Data
public class ReleaseRecordDTO {

    private Long id;

    private String version;

    private boolean successful;

    private String releaseLog;

    private Long operator;

    private Date releaseTime;
}
