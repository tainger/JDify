package io.terminus.dalaran.console.service.impl;

import io.terminus.dalaran.console.convertor.FlowConvertor;
import io.terminus.dalaran.console.model.ReleaseRequestDTO;
import io.terminus.dalaran.console.model.dto.ReleaseRecordDTO;
import io.terminus.dalaran.console.model.dto.flow.TriggerFlowDTO;
import io.terminus.dalaran.console.repository.*;
import io.terminus.dalaran.console.service.ReleaseService;
import io.terminus.dalaran.core.resource.entity.basic.BasicEntity;
import io.terminus.dalaran.core.resource.entity.common.ReleaseRecordEntity;
import io.terminus.dalaran.core.resource.entity.released.*;
import io.terminus.dalaran.core.resource.repository.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@Transactional
public class ReleaseServiceImpl implements ReleaseService {

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
    private ReleaseRecordRepository releaseRecordRepository;

    @Autowired
    private FunctionRepository functionRepository;

    @Autowired
    private FunctionReleasedRepository functionReleasedRepository;

    private final FlowConvertor flowConvertor = new FlowConvertor();

    @Override
    public ReleaseRecordDTO release(ReleaseRequestDTO requestDTO) {
        ReleaseRecordEntity enabledReleaseEntity = releaseRecordRepository.findByEnabledTrue();
        if (enabledReleaseEntity != null) {
            enabledReleaseEntity.setEnabled(false);
            releaseRecordRepository.save(enabledReleaseEntity);
        }

        ReleaseRecordEntity recordEntity = new ReleaseRecordEntity();
        recordEntity.setEnabled(true);
        recordEntity.setVersion(requestDTO.getVersion());
        recordEntity.setReleaseLog(requestDTO.getReleaseLog());
        recordEntity.setReleaseTime(new Date());
        // TODO 需要校验是否有误, 暂时没做
        recordEntity.setSuccessful(true);
        releaseRecordRepository.save(recordEntity);

        List<TriggerFlowReleasedEntity> releasedTriggerFlowEntities = toReleasedData(triggerFlowRepository.findAll(), TriggerFlowReleasedEntity.class, requestDTO.getVersion());
        triggerFlowReleasedRepository.save(releasedTriggerFlowEntities);

        List<SubFlowReleasedEntity> releasedSubFlowEntities = toReleasedData(subFlowRepository.findAll(), SubFlowReleasedEntity.class, requestDTO.getVersion());
        subFlowReleasedRepository.save(releasedSubFlowEntities);

        List<ModelReleasedEntity> releasedModelEntities = toReleasedData(modelRepository.findAll(), ModelReleasedEntity.class, requestDTO.getVersion());
        modelReleasedRepository.save(releasedModelEntities);

        List<ConnectorReleasedEntity> releasedConnectorEntities = toReleasedData(connectorRepository.findAll(), ConnectorReleasedEntity.class, requestDTO.getVersion());
        connectorReleasedRepository.save(releasedConnectorEntities);

        List<PropertyReleasedEntity> releasedPropertyEntities = toReleasedData(propertyRepository.findAll(), PropertyReleasedEntity.class, requestDTO.getVersion());
        propertyReleasedRepository.save(releasedPropertyEntities);

        List<FunctionReleasedEntity> releasedFunctionEntities = toReleasedData(functionRepository.findAll(), FunctionReleasedEntity.class, requestDTO.getVersion());
        functionReleasedRepository.save(releasedFunctionEntities);

        return toDTO(recordEntity);
    }

    @Override
    public ReleaseRecordDTO rollback(String version) {
        ReleaseRecordEntity enabledReleaseEntity = releaseRecordRepository.findByEnabledTrue();
        if (enabledReleaseEntity != null) {
            enabledReleaseEntity.setEnabled(false);
            releaseRecordRepository.save(enabledReleaseEntity);
        }
        ReleaseRecordEntity nextReleaseRecord = releaseRecordRepository.findByVersion(version);
        if (nextReleaseRecord != null) {
            nextReleaseRecord.setEnabled(true);
            releaseRecordRepository.save(nextReleaseRecord);
        }
        return toDTO(nextReleaseRecord);
    }

    @Override
    public List<TriggerFlowDTO> listReleasedTriggerFlowDTO(String version) {
        return triggerFlowReleasedRepository.findByVersion(version).stream().map(flowConvertor::toDTO).collect(Collectors.toList());
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
