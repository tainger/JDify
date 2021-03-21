package io.terminus.dalaran.config;

import com.alibaba.fastjson.annotation.JSONField;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;
import org.jetbrains.annotations.NotNull;

@Data
public abstract class AbstractComponentInfo implements ComponentInfo {

    private String type;

    private String name;

    private int order;

    private DalaranConfigField[] configFields;

    private boolean outdated;

    private String connectorType;

    private String limiterType;

    @NotNull
    private String modelType;

    @NotNull
    private String origin;

    @JSONField(serialize = false)
    @JsonIgnore
    private ConnectorInfo connectorInfo;

    @JSONField(serialize = false)
    @JsonIgnore
    private LimiterInfo limiterInfo;

    @JSONField(serialize = false)
    @JsonIgnore
    private Class configType;

    private String description;
}
