package io.terminus.dalaran.config;

import com.alibaba.fastjson.annotation.JSONField;
import io.terminus.dalaran.model.BodyType;
import lombok.Data;
import org.jetbrains.annotations.NotNull;

@Data
public abstract class AbstractComponentInfo implements ComponentInfo {

    private String type;

    private String name;

    private int order;

    private DalaranConfigField[] configFields;

    private boolean outdated;

    @NotNull
    @JSONField(serialize = false)
    private BodyType bodyType;

    @JSONField(serialize = false)
    private ConnectorInfo connectorInfo;

    @JSONField(serialize = false)
    private Class configType;
}
