package io.terminus.dalaran.console.model;

import lombok.Data;

@Data
public class ReleaseRequestDTO {

    private String version;

    private String releaseLog;
}
