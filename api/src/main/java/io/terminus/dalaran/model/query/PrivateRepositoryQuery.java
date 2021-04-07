package io.terminus.dalaran.model.query;

import lombok.Data;

@Data
public class PrivateRepositoryQuery {

    private String id;

    private String name;

    private String type;

    private String origin;

    private String tenantCode;

    private String version;

    public PrivateRepositoryQuery() {
    }

    public PrivateRepositoryQuery(String name, String type) {
        this.name = name;
        this.type = type;
    }

    public PrivateRepositoryQuery(String name, String version, String type) {
        this.name = name;
        this.type = type;
        this.version = version;
    }
}
