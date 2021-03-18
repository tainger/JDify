package io.terminus.dalaran.console.model;

import io.terminus.dalaran.console.entity.*;
import io.terminus.dalaran.core.resource.entity.TriggerFlowAbstractEntity;
import lombok.Data;

import java.util.Map;

@Data
public class TemplateData extends TriggerFlowAbstractEntity {

    private Map<String, ModelEntity> relationModel;

    private Map<String, ConnectorEntity> relationConnector;

    private Map<String, ServiceEntity> relationService;

    private Map<String, FunctionEntity> relationFunction;

    private Map<String, SubFlowEntity> relationSubFlow;
}
