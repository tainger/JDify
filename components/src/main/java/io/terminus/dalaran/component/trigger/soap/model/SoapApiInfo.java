package io.terminus.dalaran.component.trigger.soap.model;

import io.terminus.dalaran.component.common.HttpMethod;
import io.terminus.dalaran.component.trigger.rest.BasicHttpListenerConfig;
import io.terminus.dalaran.model.MessageModel;
import io.terminus.dalaran.model.flow.TriggerFlow;
import lombok.Data;

@Data
public class SoapApiInfo {

    private String name;

    private String moduleName;

    private String description;

    private String path;

    private HttpMethod method;

    private MessageModel input;

    private MessageModel output;

    public SoapApiInfo(TriggerFlow flow) {
        BasicHttpListenerConfig listenerConfig = (BasicHttpListenerConfig) flow.getTriggerConfig();
        this.name = flow.getName();
        this.path = listenerConfig.getPath();
        this.method = listenerConfig.getMethod();
        this.input = flow.getInModel();
        this.output = flow.getOutModel();
    }
}
