package io.terminus.dalaran.model.query;

import lombok.Data;

@Data
public class ResourceQuery {

    private String id;

    private String version;

    private String name;

    private String type;

    private String resourceGroup;

    private String tenantCode;

    public ResourceQuery(String id, String version, String tenantCode) {
        this.id = id;
        this.version = version;
        this.tenantCode = tenantCode;
    }
}
