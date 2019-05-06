package io.terminus.dalaran;

import io.terminus.dalaran.entity.manage.ReleaseRecordEntity;
import io.terminus.dalaran.entity.release.*;
import io.terminus.dalaran.model.config.TriggerInfo;
import io.terminus.dalaran.model.flow.SubFlow;
import io.terminus.dalaran.model.flow.TriggerFlow;
import io.terminus.dalaran.repository.release.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;

import javax.annotation.PostConstruct;

@Slf4j
public class ReleasedFlowLoader extends AbstractDalaranLoader<TriggerFlowReleasedEntity, SubFlowReleasedEntity> {
    @Autowired
    private TriggerFlowReleasedRepository releasedTriggerFlowRepository;

    @Autowired
    private SubFlowReleasedRepository releasedSubFlowRepository;

    @Autowired
    private ModelReleasedRepository modelRepository;

    @Autowired
    private ReleaseRecordRepository releaseRecordRepository;

    @Autowired
    private ConnectorReleasedRepository connectorRepository;

    @Autowired
    private PropertyReleasedRepository propertyRepository;

    @Autowired
    private DalaranContext dalaranContext;

    private String version;

    // TODO 临时每分钟 load 一下...
    @PostConstruct
    @Scheduled(cron = "0 * * * * *")
    private void init() {
        ReleaseRecordEntity recordEntity = releaseRecordRepository.findByEnabledTrue();
        synchronized (this) {
            if (recordEntity == null || recordEntity.getVersion().equals(version)) {
                log.debug("version not change");
                return;
            }
            version = recordEntity.getVersion();
            log.info(">>>>>>>>>>========= load released flow start, version {}", recordEntity.getVersion());
            for (TriggerFlowReleasedEntity flowEntity : releasedTriggerFlowRepository.findByVersion(version)) {
                loadTriggerFlow(flowEntity);
                log.info("load released flow[{}] ", flowEntity.getOriginId());
            }
            log.info("<<<<<<<<<<========= load released flow end, version {}", recordEntity.getVersion());
        }
    }

    @Override
    public TriggerFlow loadTriggerFlow(TriggerFlowReleasedEntity flowEntity) {
        TriggerFlow flow = super.loadTriggerFlow(flowEntity);
        TriggerInfo triggerInfo = dalaranContext.getDalaranComponentContext().getTriggerInfo(flowEntity.getTriggerType());
        Object triggerConfig = buildConfig(triggerInfo, flowEntity.getTriggerConfig());
        flow.setTriggerType(flowEntity.getTriggerType());
        flow.setTriggerConfig(triggerConfig);
        flow.setId(flowEntity.getOriginId());
        dalaranContext.addTriggerFlow(flow);
        return flow;
    }

    @Override
    public SubFlow loadSubFlow(SubFlowReleasedEntity flowEntity) {
        SubFlow flow = super.loadSubFlow(flowEntity);
        flow.setId(flowEntity.getOriginId());
        return flow;
    }

    @Override
    public ConnectorReleasedEntity getConnector(Long connectorId) {
        return connectorRepository.findByVersionAndOriginId(version, connectorId);
    }

    @Override
    public ModelReleasedEntity getModelEntity(Long modelId) {
        return modelRepository.findByVersionAndOriginId(version, modelId);
    }

    @Override
    protected PropertyReleasedEntity[] getPropertyEntities() {
        return propertyRepository.findByVersion(version).toArray(new PropertyReleasedEntity[0]);
    }

}
