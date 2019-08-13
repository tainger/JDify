package io.terminus.dalaran.console;

import io.terminus.dalaran.console.entity.FunctionEntity;
import io.terminus.dalaran.console.entity.SubFlowEntity;
import io.terminus.dalaran.console.entity.TriggerFlowEntity;
import io.terminus.dalaran.core.context.DalaranContext;
import io.terminus.dalaran.core.resource.DalaranResourceBuilder;
import io.terminus.dalaran.core.resource.entity.SubFlowAbstractEntity;
import io.terminus.dalaran.core.resource.entity.TriggerFlowAbstractEntity;
import io.terminus.dalaran.core.resource.repository.ReleaseRecordRepository;
import io.terminus.dalaran.model.flow.BasicFlow;
import io.terminus.dalaran.model.flow.FlowStatus;
import io.terminus.dalaran.model.flow.SubFlow;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;

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
        BasicFlow testFlow = resourceBuilder.buildTestFlow(triggerFlowEntity);
        dalaranContext.addTestFlow(testFlow);
    }

    public void reloadTestSubFlow(Long subFlowId) {
        SubFlowAbstractEntity subFlowEntity = resourceLoader.loadSubFlow(subFlowId);
        SubFlow subFlow = resourceBuilder.buildSubFlow(subFlowEntity);
        dalaranContext.addSubFlow(subFlow);
    }

    // TODO 延时 5 秒, 因为目前 Component 的加载是根据 Spring Bean 的初始化, 有时候初始化流时, Component 还没有 ready
    // TODO 组件需要更好的加载方式, 更早加载或者有机制确保加载完成在初始化流
    @PostConstruct
    private void init() {
        Timer timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                List<TriggerFlowEntity> triggerFlows = resourceLoader.loadAllTriggerFlow();
                for (TriggerFlowEntity triggerFlowEntity : triggerFlows) {
                    // warning 的也可以加载
                    if (triggerFlowEntity.getStatus() != FlowStatus.Error) {
                        BasicFlow testFlow = resourceBuilder.buildTriggerFlow(triggerFlowEntity);
                        dalaranContext.addTestFlow(testFlow);
                        log.info("load test flow {}", testFlow.getId());
                    } else {
                        log.info("can't load test flow [{}], because has error.", triggerFlowEntity.getId());
                    }
                }
                List<SubFlowEntity> subFlows = resourceLoader.loadAllSubFlow();
                for (SubFlowEntity subFlowEntity : subFlows) {
                    // warning 的也可以加载
                    if (subFlowEntity.getStatus() != FlowStatus.Error) {
                        SubFlow testFlow = resourceBuilder.buildSubFlow(subFlowEntity);
                        dalaranContext.addSubFlow(testFlow);
                        log.info("load test sub-flow {}", testFlow.getId());
                    } else {
                        log.info("can't load test sub-flow [{}], because has error.", subFlowEntity.getId());
                    }
                }
                List<FunctionEntity> functions = resourceLoader.loadAllFunctions();
                for (FunctionEntity function : functions) {
                    dalaranContext.getDalaranFunctionContext().addCustomFunction(function.getId(), function.getType(),
                            function.getScript(), function.getParams());
                }
            }
        }, 10 * 1000L);
    }
}
