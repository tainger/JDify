package io.terminus.dalaran.console;

import io.terminus.dalaran.AbstractDalaranLoader;
import io.terminus.dalaran.DalaranContext;
import io.terminus.dalaran.entity.ConnectorEntity;
import io.terminus.dalaran.entity.ModelEntity;
import io.terminus.dalaran.entity.flow.SubFlowEntity;
import io.terminus.dalaran.entity.flow.TriggerFlowEntity;
import io.terminus.dalaran.model.flow.SubFlow;
import io.terminus.dalaran.model.flow.TriggerFlow;
import io.terminus.dalaran.repository.ConnectorRepository;
import io.terminus.dalaran.repository.ModelRepository;
import io.terminus.dalaran.repository.SubFlowRepository;
import io.terminus.dalaran.repository.TriggerFlowRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;

import javax.annotation.PostConstruct;

@Slf4j
public class TestFlowLoader extends AbstractDalaranLoader<TriggerFlowEntity, SubFlowEntity> {
    @Autowired
    private TriggerFlowRepository triggerFlowRepository;

    @Autowired
    private SubFlowRepository subFlowRepository;

    @Autowired
    private ModelRepository modelRepository;

    @Autowired
    private ConnectorRepository connectorRepository;

    @Autowired
    private DalaranContext dalaranContext;

    @PostConstruct
    private void loadAllTestFlow() {
        for (TriggerFlowEntity flowEntity : triggerFlowRepository.findAll()) {
            loadTriggerFlow(flowEntity);
            log.info("load flow[{}]", flowEntity.getId());
        }
    }

    @Override
    public TriggerFlow loadTriggerFlow(TriggerFlowEntity flowEntity) {
        TriggerFlow flow = super.loadTriggerFlow(flowEntity);
        flow.setId(flowEntity.getId());
        dalaranContext.addTestFlow(flow);
        return flow;
    }


    @Override
    public SubFlow loadSubFlow(SubFlowEntity flowEntity) {
        SubFlow flow = super.loadSubFlow(flowEntity);
        flow.setId(flowEntity.getId());
        dalaranContext.addTestFlow(flow);
        return flow;
    }

    @Override
    public ConnectorEntity getConnector(Long connectorId) {
        return connectorRepository.findOne(connectorId);
    }

    @Override
    public ModelEntity getModelEntity(Long modelId) {
        return modelRepository.findOne(modelId);
    }
}
