package io.terminus.dalaran.console.service;

import io.terminus.dalaran.console.model.FlowModel;
import io.terminus.dalaran.console.model.query.FlowQuery;
import io.terminus.dalaran.console.model.query.rst.ModuleComponent;
import io.terminus.dalaran.entity.FlowEntity;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public interface FlowManagementService {

    Long saveFlow(FlowEntity flowEntity);

    Long createFlow(FlowModel flowModel);

    void deleteFlow(Long flowId);

    FlowModel updateFlow(FlowModel flowModel);

    List<FlowModel> queryFlows(FlowQuery query);

    List<FlowModel> list();

    List<FlowModel> queryByProcessorIds(List<Long> processorIds);

    List<ModuleComponent> getComponents(Long moduleId);

    @Nullable
    FlowModel getById(Long flowId);
}
