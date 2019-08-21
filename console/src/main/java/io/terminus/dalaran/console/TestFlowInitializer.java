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
        dalaranContext.addTestSubFLow(subFlow);
    }

    // TODO 延时 5 秒, 因为目前 Component 的加载是根据 Spring Bean 的初始化, 有时候初始化流时, Component 还没有 ready
    // TODO 组件需要更好的加载方式, 更早加载或者有机制确保加载完成在初始化流
    @PostConstruct
    private void init() {
        Timer timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                loadResources();
            }
        }, 10 * 1000L);
    }

    public void loadResources() {
        List<FunctionEntity> functions = resourceLoader.loadAllFunctions();
        for (FunctionEntity function : functions) {
            dalaranContext.getDalaranFunctionContext().addCustomFunction(function.getId(), function.getType(),
                    function.getScript(), function.getParams());
        }
        List<TriggerFlowEntity> triggerFlows = resourceLoader.loadAvailableTriggerFlow();
        for (TriggerFlowEntity triggerFlowEntity : triggerFlows) {
            try {
                BasicFlow testFlow = resourceBuilder.buildTriggerFlow(triggerFlowEntity);
                dalaranContext.addTestFlow(testFlow);
                log.info("load test flow [{}]", testFlow.getId());
            } catch (Throwable e) {
                e.printStackTrace();
                log.error("load test flow [{}] error", triggerFlowEntity.getId());
            }
        }
        List<SubFlowEntity> subFlows = resourceLoader.loadAvailableSubFlow();
        for (SubFlowEntity subFlowEntity : subFlows) {
            try {
                SubFlow testFlow = resourceBuilder.buildSubFlow(subFlowEntity);
                dalaranContext.addSubFlow(testFlow);
                dalaranContext.addTestSubFLow(testFlow);
                log.info("load sub-flow {}", testFlow.getId());
            } catch (Throwable e) {
                e.printStackTrace();
                log.error("load sub flow [{}] error", subFlowEntity.getId());
            }
        }
    }
}
