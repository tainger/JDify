package io.terminus.dalaran.console;

import io.terminus.dalaran.console.entity.TriggerFlowEntity;
import io.terminus.dalaran.core.context.DalaranContext;
import io.terminus.dalaran.core.flow.model.SubFlow;
import io.terminus.dalaran.core.flow.model.TriggerFlow;
import io.terminus.dalaran.core.resource.DalaranResourceBuilder;
import io.terminus.dalaran.core.resource.entity.SubFlowAbstractEntity;
import io.terminus.dalaran.core.resource.entity.TriggerFlowAbstractEntity;
import io.terminus.dalaran.core.resource.repository.ReleaseRecordRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.Ordered;

import javax.annotation.PostConstruct;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

@Slf4j
public class TestFlowInitializer {

    @Autowired
    private ReleaseRecordRepository releaseRecordRepository;

    @Autowired
    private TestResourceLoader resourceLoader;

    @Autowired
    private DalaranResourceBuilder resourceBuilder;

    @Autowired
    private DalaranContext dalaranContext;

    public void reloadTestTriggerFlow(Long triggerFlowId) {
        TriggerFlowAbstractEntity triggerFlowEntity = resourceLoader.loadTriggerFlow(triggerFlowId);
        TriggerFlow triggerFlow = resourceBuilder.buildTriggerFlow(triggerFlowEntity);
        dalaranContext.addTestFlow(triggerFlow);
    }

    public void reloadTestSubFlow(Long subFlowId) {
        SubFlowAbstractEntity subFlowEntity = resourceLoader.loadSubFlow(subFlowId);
        SubFlow subFlow = resourceBuilder.buildSubFlow(subFlowEntity);
        dalaranContext.addTestFlow(subFlow);
    }

    @PostConstruct
    private void init() {
        Timer timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                List<TriggerFlowEntity> triggerFlows = resourceLoader.loadAllTriggerFlow();
                for (TriggerFlowEntity triggerFlowEntity : triggerFlows) {
                    TriggerFlow triggerFlow = resourceBuilder.buildTriggerFlow(triggerFlowEntity);
                    dalaranContext.addTestFlow(triggerFlow);
                    log.info("load test flow {}", triggerFlow.getId());
                }
            }
        }, 5000L);


        // TODO load sub flow
    }
}
