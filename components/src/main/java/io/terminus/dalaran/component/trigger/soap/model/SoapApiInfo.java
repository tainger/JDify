package io.terminus.dalaran.component.trigger.soap.model;

import io.terminus.dalaran.component.common.HttpMethod;
import io.terminus.dalaran.component.trigger.soap.SoapListenerConfig;
import io.terminus.dalaran.core.resource.entity.ModelAbstractEntity;
import lombok.Data;

@Data
public class SoapApiInfo {

    private String name;

    private String moduleName;

    private String description;

    private String path;

    private HttpMethod method;

    private SoapModel input;

    private SoapModel output;

    public SoapApiInfo(String name, SoapListenerConfig listenerConfig, SoapModel input, SoapModel output) {
        this.name = name;
        this.path = listenerConfig.getPath();
        this.method = listenerConfig.getMethod();
        this.input = input;
        this.output = output;
    }
}
