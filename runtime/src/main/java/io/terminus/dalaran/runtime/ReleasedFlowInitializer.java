package io.terminus.dalaran.runtime;

import io.terminus.dalaran.core.context.DalaranContext;
import io.terminus.dalaran.core.resource.DalaranResourceBuilder;
import io.terminus.dalaran.core.resource.entity.common.ReleaseRecordEntity;
import io.terminus.dalaran.core.resource.entity.released.FunctionReleasedEntity;
import io.terminus.dalaran.core.resource.entity.released.SubFlowReleasedEntity;
import io.terminus.dalaran.core.resource.entity.released.TriggerFlowReleasedEntity;
import io.terminus.dalaran.core.resource.repository.ReleaseRecordRepository;
import io.terminus.dalaran.model.flow.FlowStatus;
import io.terminus.dalaran.model.flow.SubFlow;
import io.terminus.dalaran.model.flow.TriggerFlow;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;

import java.util.List;

@Slf4j
public class ReleasedFlowInitializer {

    @Autowired
    private ReleaseRecordRepository releaseRecordRepository;

    @Autowired
    private ReleasedResourceLoader resourceLoader;

    @Autowired
    private DalaranResourceBuilder resourceBuilder;

    @Autowired
    private DalaranContext dalaranContext;

    // TODO 临时每分钟 load 一下...
    // TODO 启动延时 5 秒, 因为目前 Component 的加载是根据 Spring Bean 的初始化, 有时候初始化流时, Component 还没有 ready
    // TODO 组件需要更好的加载方式, 更早加载或者有机制确保加载完成在初始化流
    @Scheduled(fixedDelay = 60 * 1000L, initialDelay = 5 * 1000L)
    private void init() {
        ReleaseRecordEntity recordEntity = releaseRecordRepository.findByEnabledTrue();
        synchronized (this) {
            if (recordEntity == null || recordEntity.getVersion().equals(resourceLoader.getVersion())) {
                log.debug("version not change");
                return;
            }
            resourceLoader.setVersion(recordEntity.getVersion());

            List<FunctionReleasedEntity> functions = resourceLoader.loadAllFunctions();
            for (FunctionReleasedEntity function : functions) {
                dalaranContext.getDalaranFunctionContext().addCustomFunction(function.getId(), function.getType(),
                        function.getScript(), function.getParams());
            }

            List<TriggerFlowReleasedEntity> triggerFlows = resourceLoader.loadAllTriggerFlow();
            for (TriggerFlowReleasedEntity triggerFlowEntity : triggerFlows) {
                if (triggerFlowEntity.getStatus() != FlowStatus.Error) {
                    TriggerFlow triggerFlow = resourceBuilder.buildTriggerFlow(triggerFlowEntity);
                    dalaranContext.addTriggerFlow(triggerFlow);
                    log.info("load released flow [{}]", triggerFlow.getId());
                } else {
                    log.info("can't load flow [{}], because has error.", triggerFlowEntity.getId());
                }
            }

            List<SubFlowReleasedEntity> subFLows = resourceLoader.loadAllSubFlow();
            for (SubFlowReleasedEntity subFlowEntity : subFLows) {
                if (subFlowEntity.getStatus() != FlowStatus.Error) {
                    SubFlow subFlow = resourceBuilder.buildSubFlow(subFlowEntity);
                    dalaranContext.addSubFlow(subFlow);
                    log.info("load released sub-flow {}", subFlow.getId());
                } else {
                    log.info("can't load sub-flow [{}], because has error.", subFlowEntity.getId());
                }
            }
        }
    }
}
