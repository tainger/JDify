package io.terminus.dalaran.model;

import lombok.Data;

@Data
public class ResourceUploadRequest {

    private String name;

    private String type;

    private String version;

    private String resourceGroup;

    private String filePath;
}
