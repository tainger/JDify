package io.terminus.dalaran.console;

import io.terminus.dalaran.console.entity.FunctionEntity;
import io.terminus.dalaran.console.entity.SubFlowEntity;
import io.terminus.dalaran.console.entity.TriggerFlowEntity;
import io.terminus.dalaran.core.context.DalaranContext;
import io.terminus.dalaran.core.market.MarketResourceLoader;
import io.terminus.dalaran.core.oss.OSSAccount;
import io.terminus.dalaran.core.resource.DalaranResourceBuilder;
import io.terminus.dalaran.core.resource.DalaranStarter;
import io.terminus.dalaran.core.resource.entity.SubFlowAbstractEntity;
import io.terminus.dalaran.core.resource.entity.TriggerFlowAbstractEntity;
import io.terminus.dalaran.core.resource.entity.common.PrivateRepositoryEntity;
import io.terminus.dalaran.core.resource.repository.ReleaseRecordRepository;
import io.terminus.dalaran.model.flow.BasicFlow;
import io.terminus.dalaran.model.flow.SubFlow;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

@Slf4j
public class TestFlowInitializer implements DalaranStarter {

    @Autowired
    private ReleaseRecordRepository releaseRecordRepository;

    @Autowired
    private TestResourceLoader resourceLoader;

    @Autowired
    private DalaranResourceBuilder resourceBuilder;

    @Autowired
    private MarketResourceLoader marketResourceLoader;

    @Autowired
    private OSSAccount ossAccount;

    @Autowired
    private DalaranContext dalaranContext;

    public void reloadTestTriggerFlow(String triggerFlowId) {
        TriggerFlowAbstractEntity triggerFlowEntity = resourceLoader.loadTriggerFlow(triggerFlowId);
        BasicFlow testFlow = resourceBuilder.buildTestFlow(triggerFlowEntity);
        dalaranContext.addTestFlow(testFlow);
    }

    public void reloadTestSubFlow(String subFlowId) {
        SubFlowAbstractEntity subFlowEntity = resourceLoader.loadSubFlow(subFlowId);
        SubFlow subFlow = resourceBuilder.buildSubFlow(subFlowEntity);
        dalaranContext.addSubFlow(subFlow);
        dalaranContext.addTestSubFLow(subFlow);
    }

    @Override
    public void start() {
        log.info("dalaran resource load start");
        List<FunctionEntity> functions = resourceLoader.loadAllFunctions();
        for (FunctionEntity function : functions) {
            try {
                dalaranContext.getDalaranFunctionContext().addCustomFunction(String.valueOf(function.getResourceKey()), function.getType(),
                        function.getScript(), function.getParams());
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        List<PrivateRepositoryEntity> privateRepositoryEntityList = resourceLoader.loadPackage();
        for (PrivateRepositoryEntity entity : privateRepositoryEntityList) {
            try {
//                ResourceFile resourceFile = JSON.parseObject(entity.getData(), ResourceFile.class);
//                File file = OSSUtils.downloadByPath(resourceFile.getFilePath(), ossAccount);
////                String origin = entity.getOrigin();
////                if (StringUtils.equalsIgnoreCase(origin, "PRIVATE")) {
////                    origin = "CUSTOM";
////                }
//                marketResourceLoader.install(file, entity.getOrigin(), entity.getVersion());
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        List<TriggerFlowEntity> triggerFlows = resourceLoader.loadAvailableTriggerFlow();
        for (TriggerFlowEntity triggerFlowEntity : triggerFlows) {
            try {
                BasicFlow testFlow = resourceBuilder.buildTriggerFlow(triggerFlowEntity);
                dalaranContext.addTestFlow(testFlow);
                log.info("load test flow [{}]", testFlow.getId());
            } catch (Throwable e) {
                e.printStackTrace();
                log.error("load test flow [{}] error", triggerFlowEntity.getResourceKey());
            }
        }
        List<SubFlowEntity> subFlows = resourceLoader.loadAvailableSubFlow();
        for (SubFlowEntity subFlowEntity : subFlows) {
            try {
                SubFlow testFlow = resourceBuilder.buildSubFlow(subFlowEntity);
                // TODO 子流程内的片段会重复加载
                dalaranContext.addSubFlow(testFlow);
                dalaranContext.addTestSubFLow(testFlow);
                log.info("load sub-flow {}", testFlow.getId());
            } catch (Throwable e) {
                e.printStackTrace();
                log.error("load sub flow [{}] error", subFlowEntity.getResourceKey());
            }
        }

        log.info("dalaran resource load started");
    }
}
