package io.terminus.dalaran.model.query;

import lombok.Data;

@Data
public class PrivateRepositoryQuery {

    private String id;

    private String type;

    private String origin;

    private String tenantCode;
}
