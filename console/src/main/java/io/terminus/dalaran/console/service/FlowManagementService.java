package io.terminus.dalaran.console.service;

import io.terminus.dalaran.console.entity.FlowEntity;
import io.terminus.dalaran.console.model.FlowModel;
import io.terminus.dalaran.console.model.query.FlowQuery;

import java.util.List;

public interface FlowManagementService {

    void saveFlow(FlowEntity flowEntity);

    void publish();

    void createFlow(FlowModel flowModel);

    void deleteFlow(Long flowId);

    void updateFlow(FlowModel flowModel);

    List<FlowModel> queryFlows(FlowQuery query);

    List<FlowModel> list();
}
