package io.terminus.dalaran.console.model;

import io.terminus.dalaran.console.entity.*;
import io.terminus.dalaran.core.resource.entity.TriggerFlowAbstractEntity;
import io.terminus.dalaran.model.market.MarketProcessor;
import lombok.Data;

import java.util.HashMap;
import java.util.Map;

@Data
public class TemplateData extends TriggerFlowAbstractEntity {

    private Map<String, ModelEntity> relationModel = new HashMap<>();

    private Map<String, ConnectorEntity> relationConnector = new HashMap<>();

    private Map<String, ServiceEntity> relationService = new HashMap<>();

    private Map<String, FunctionEntity> relationFunction = new HashMap<>();

    private Map<String, SubFlowEntity> relationSubFlow = new HashMap<>();

    private Map<String, MarketProcessor> relationPackage = new HashMap<>();
}
