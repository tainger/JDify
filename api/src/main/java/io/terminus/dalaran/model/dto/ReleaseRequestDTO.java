package io.terminus.dalaran.model.dto;

import lombok.Data;

@Data
public class ReleaseRequestDTO {

    private String version;

    private String releaseLog;
}
