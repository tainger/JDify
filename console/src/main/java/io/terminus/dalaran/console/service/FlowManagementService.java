package io.terminus.dalaran.console.service;

import io.terminus.dalaran.console.model.FlowModel;
import io.terminus.dalaran.console.model.query.FlowQuery;
import io.terminus.dalaran.console.model.query.rst.ModuleComponent;
import io.terminus.dalaran.model.config.ProcessorInfo;
import io.terminus.dalaran.model.config.TriggerInfo;
import io.terminus.dalaran.entity.FlowEntity;
import java.util.Collection;
import org.jetbrains.annotations.Nullable;
import java.util.List;

public interface FlowManagementService {

    void saveFlow(FlowEntity flowEntity);

    void createFlow(FlowModel flowModel);

    void deleteFlow(Long flowId);

    void updateFlow(FlowModel flowModel);

    List<FlowModel> queryFlows(FlowQuery query);

    List<FlowModel> list();

    List<FlowModel> queryByProcessorIds(List<Long> processorIds);

    Collection<ProcessorInfo> listProcessors();

    Collection<TriggerInfo> listTriggers();

    List<ModuleComponent> getComponents(Long moduleId);

    @Nullable
    FlowModel getById(Long flowId);
}
