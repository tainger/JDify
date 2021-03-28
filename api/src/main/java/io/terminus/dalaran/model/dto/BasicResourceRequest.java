package io.terminus.dalaran.model.dto;

import lombok.Data;

@Data
public class BasicResourceRequest {

    private String id;

    private String version;

    private String type;

    private String moduleId;
}
