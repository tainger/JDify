package io.terminus.dalaran.runtime;

import io.terminus.dalaran.core.context.DalaranContext;
import io.terminus.dalaran.core.flow.model.TriggerFlow;
import io.terminus.dalaran.core.resource.DalaranResourceBuilder;
import io.terminus.dalaran.core.resource.entity.common.ReleaseRecordEntity;
import io.terminus.dalaran.core.resource.entity.released.TriggerFlowReleasedEntity;
import io.terminus.dalaran.core.resource.repository.ReleaseRecordRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;

import javax.annotation.PostConstruct;
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
    @PostConstruct
    @Scheduled(cron = "0 * * * * *")
    private void init() {
        ReleaseRecordEntity recordEntity = releaseRecordRepository.findByEnabledTrue();
        synchronized (this) {
            if (recordEntity == null || recordEntity.getVersion().equals(resourceLoader.getVersion())) {
                log.debug("version not change");
                return;
            }
            resourceLoader.setVersion(recordEntity.getVersion());

            List<TriggerFlowReleasedEntity> triggerFlows = resourceLoader.loadAllTriggerFlow();
            for (TriggerFlowReleasedEntity triggerFlowEntity : triggerFlows) {
                TriggerFlow triggerFlow = resourceBuilder.buildTriggerFlow(triggerFlowEntity);
                dalaranContext.addTriggerFlow(triggerFlow);
                log.info("load released flow {}", triggerFlow.getId());
            }

            // TODO load sub flow
        }
    }
}
