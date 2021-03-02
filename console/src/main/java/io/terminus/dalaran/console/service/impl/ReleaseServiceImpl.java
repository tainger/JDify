package io.terminus.dalaran.console.service.impl;

import io.terminus.dalaran.console.convertor.FlowConvertor;
import io.terminus.dalaran.console.entity.LimiterEntity;
import io.terminus.dalaran.console.repository.*;
import io.terminus.dalaran.console.service.ReleaseService;
import io.terminus.dalaran.core.resource.entity.basic.BasicEntity;
import io.terminus.dalaran.core.resource.entity.common.ModuleEntity;
import io.terminus.dalaran.core.resource.entity.common.ReleaseRecordEntity;
import io.terminus.dalaran.core.resource.entity.released.*;
import io.terminus.dalaran.core.resource.repository.*;
import io.terminus.dalaran.model.dto.ReleaseRecordDTO;
import io.terminus.dalaran.model.dto.ReleaseRequestDTO;
import io.terminus.dalaran.model.dto.flow.ReleaseFlowDTO;
import io.terminus.dalaran.model.dto.flow.TriggerFlowDTO;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.common.protocol.types.Field;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
@Transactional
public class ReleaseServiceImpl implements ReleaseService {

    @Autowired
    private ReleaseRecordRepository releaseRecordRepository;

    @Autowired
    private TriggerFlowRepository triggerFlowRepository;

    @Autowired
    private SubFlowRepository subFlowRepository;

    @Autowired
    private ModelRepository modelRepository;

    @Autowired
    private ConnectorRepository connectorRepository;

    @Autowired
    private PropertyRepository propertyRepository;

    @Autowired
    private FunctionRepository functionRepository;

    @Autowired
    private ClientRepository clientRepository;

    @Autowired
    private ServiceRepository serviceRepository;

    @Autowired
    private TriggerFlowReleasedRepository triggerFlowReleasedRepository;

    @Autowired
    private SubFlowReleasedRepository subFlowReleasedRepository;

    @Autowired
    private ModelReleasedRepository modelReleasedRepository;

    @Autowired
    private ConnectorReleasedRepository connectorReleasedRepository;

    @Autowired
    private PropertyReleasedRepository propertyReleasedRepository;

    @Autowired
    private FunctionReleasedRepository functionReleasedRepository;

    @Autowired
    private ClientReleasedRepository clientReleasedRepository;

    @Autowired
    private ServiceReleasedRepository serviceReleasedRepository;

    @Autowired
    private LimiterReleasedRepository limiterReleasedRepository;

    @Autowired
    private LimiterRepository limiterRepository;

    @Autowired
    private  ModuleRepository moduleRepository;

    @Autowired
    private AlarmRuleReleasedRepository alarmRuleReleasedRepository;

    @Autowired
    private AlarmRuleRepository alarmRuleRepository;

    private final FlowConvertor flowConvertor = new FlowConvertor();

    @Override
    public ReleaseRecordDTO release(ReleaseRequestDTO requestDTO) {
        ReleaseRecordEntity enabledReleaseEntity = releaseRecordRepository.findByEnabledTrue();
        String lastVersion = "";
        if (enabledReleaseEntity != null) {
            enabledReleaseEntity.setEnabled(false);
            releaseRecordRepository.save(enabledReleaseEntity);
            lastVersion = enabledReleaseEntity.getVersion();
        }
        ReleaseRecordEntity recordEntity = new ReleaseRecordEntity();
        recordEntity.setEnabled(true);
        recordEntity.setVersion(requestDTO.getVersion());
        recordEntity.setLastVersion(lastVersion);
        recordEntity.setReleaseLog(requestDTO.getReleaseLog());
        recordEntity.setReleaseTime(new Date());
        // TODO 需要校验是否有误, 暂时没做
        recordEntity.setSuccessful(true);
        releaseRecordRepository.save(recordEntity);

        List<ModuleEntity> moduleEntities = moduleRepository.findByIsExistTrue();
        List<TriggerFlowReleasedEntity> releasedTriggerFlowEntities = new ArrayList<>();
        moduleEntities.stream().forEach(moduleEntity -> releasedTriggerFlowEntities.addAll(toReleasedData(triggerFlowRepository.findByModuleIdAndIsExistTrue(moduleEntity.getId()),TriggerFlowReleasedEntity.class, requestDTO.getVersion())));
       // List<TriggerFlowReleasedEntity> releasedTriggerFlowEntities = toReleasedData(triggerFlowRepository.findAll(), TriggerFlowReleasedEntity.class, requestDTO.getVersion());
        triggerFlowReleasedRepository.saveAll(releasedTriggerFlowEntities);

        List<SubFlowReleasedEntity> releasedSubFlowEntities = toReleasedData(subFlowRepository.findByIsExistTrue(), SubFlowReleasedEntity.class, requestDTO.getVersion());
        subFlowReleasedRepository.saveAll(releasedSubFlowEntities);

        List<ModelReleasedEntity> releasedModelEntities = toReleasedData(modelRepository.findByIsExistTrue(), ModelReleasedEntity.class, requestDTO.getVersion());
        modelReleasedRepository.saveAll(releasedModelEntities);

        List<ConnectorReleasedEntity> releasedConnectorEntities = toReleasedData(connectorRepository.findByIsExistTrue(), ConnectorReleasedEntity.class, requestDTO.getVersion());
        connectorReleasedRepository.saveAll(releasedConnectorEntities);

        List<PropertyReleasedEntity> releasedPropertyEntities = toReleasedData(propertyRepository.findAll(), PropertyReleasedEntity.class, requestDTO.getVersion());
        propertyReleasedRepository.saveAll(releasedPropertyEntities);

        List<FunctionReleasedEntity> releasedFunctionEntities = toReleasedData(functionRepository.findByIsExistTrue(), FunctionReleasedEntity.class, requestDTO.getVersion());
        functionReleasedRepository.saveAll(releasedFunctionEntities);

        List<ClientReleasedEntity> releasedClientEntities = toReleasedData(clientRepository.findByIsExistTrue(), ClientReleasedEntity.class, requestDTO.getVersion());
        clientReleasedRepository.saveAll(releasedClientEntities);

        List<ServiceReleasedEntity> releasedServiceEntities = toReleasedData(serviceRepository.findByIsExistTrue(), ServiceReleasedEntity.class, requestDTO.getVersion());
        serviceReleasedRepository.saveAll(releasedServiceEntities);

        List<LimiterReleasedEntity> limiterReleasedEntities = toReleasedData(limiterRepository.findByIsExistTrue(), LimiterReleasedEntity.class, requestDTO.getVersion());
        limiterReleasedRepository.saveAll(limiterReleasedEntities);

        List<AlarmRuleReleasedEntity> alarmRuleReleasedEntities = toReleasedData(alarmRuleRepository.findByIsExistTrue(), AlarmRuleReleasedEntity.class, requestDTO.getVersion());
        alarmRuleReleasedRepository.saveAll(alarmRuleReleasedEntities);

        return toDTO(recordEntity);
    }

    @Override
    public ReleaseRecordDTO rollback(String version) {
        ReleaseRecordEntity enabledReleaseEntity = releaseRecordRepository.findByEnabledTrue();
        String lastVersion = "";
        if (enabledReleaseEntity != null) {
            enabledReleaseEntity.setEnabled(false);
            releaseRecordRepository.save(enabledReleaseEntity);
            lastVersion = enabledReleaseEntity.getVersion();
        }
        ReleaseRecordEntity nextReleaseRecord = releaseRecordRepository.findByVersion(version);
        if (nextReleaseRecord != null) {
            nextReleaseRecord.setEnabled(true);
            nextReleaseRecord.setLastVersion(lastVersion);
            releaseRecordRepository.save(nextReleaseRecord);
        }
        return toDTO(nextReleaseRecord);
    }

    @Override
    public List<ReleaseFlowDTO> listReleasedTriggerFlowDTO(String version) {
        List<ModuleEntity> moduleEntities = moduleRepository.findByIsExistTrue();
        Map<Long, String> map = new HashMap<>();
        moduleEntities.forEach(moduleEntity -> map.put(moduleEntity.getId(), moduleEntity.getName()));
        List<ReleaseFlowDTO> triggerFlowDTOS = triggerFlowReleasedRepository.findByVersion(version).stream().map(flowConvertor::releaseToDTOAndModuleName).collect(Collectors.toList());
        triggerFlowDTOS.forEach(triggerFlowDTO -> triggerFlowDTO.setModuleName(map.get(triggerFlowDTO.getModuleId())));
        return triggerFlowDTOS;
    }

    @Override
    public List<TriggerFlowReleasedEntity> listReleasedTriggerFlow(String version) {
        return triggerFlowReleasedRepository.findByVersion(version);
    }

    @Override
    public List<SubFlowReleasedEntity> listReleasedSubFlow(String version) {
        return subFlowReleasedRepository.findByVersion(version);
    }

    @Override
    public ModelReleasedEntity getReleasedModel(String version, Long modelId) {
        return modelReleasedRepository.findByVersionAndOriginId(version, modelId);
    }

    @Override
    public List<PropertyReleasedEntity> getReleasedProperty(String version) {
        return propertyReleasedRepository.findByVersion(version);
    }

    @Override
    public List<ReleaseRecordDTO> listReleaseRecordDTO() {
        return releaseRecordRepository.findAll(new Sort(Sort.Direction.DESC, "releaseTime"))
                .stream().map(this::toDTO).collect(Collectors.toList());
    }

    private <T extends ReleasedEntity, E extends BasicEntity> List<T> toReleasedData(List<E> data, Class<T> releasedType, String version) {
        return data.stream().map(entity -> {
            try {
                T releasedEntity = releasedType.newInstance();
                BeanUtils.copyProperties(entity, releasedEntity);
                releasedEntity.setId(null);
                releasedEntity.setOriginId(entity.getId());
                releasedEntity.setVersion(version);
                return releasedEntity;
            } catch (InstantiationException | IllegalAccessException e) {
                e.printStackTrace();
            }
            // TODO throw...
            return null;
        }).collect(Collectors.toList());
    }

    private ReleaseRecordDTO toDTO(ReleaseRecordEntity recordEntity) {
        ReleaseRecordDTO dto = new ReleaseRecordDTO();
        BeanUtils.copyProperties(recordEntity, dto);
        return dto;
    }
}
