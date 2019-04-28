package io.terminus.dalaran.console.service.impl;

import io.terminus.dalaran.console.convertor.FlowConvertor;
import io.terminus.dalaran.console.model.ReleaseRequestDTO;
import io.terminus.dalaran.console.model.dto.ReleaseRecordDTO;
import io.terminus.dalaran.console.model.dto.flow.TriggerFlowDTO;
import io.terminus.dalaran.console.service.ReleaseService;
import io.terminus.dalaran.entity.release.ReleaseRecordEntity;
import io.terminus.dalaran.entity.release.ReleasedModelEntity;
import io.terminus.dalaran.entity.release.ReleasedSubFlowEntity;
import io.terminus.dalaran.entity.release.ReleasedTriggerFlowEntity;
import io.terminus.dalaran.repository.*;
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
    private ReleasedTriggerFlowRepository releasedTriggerFlowRepository;

    @Autowired
    private ReleasedSubFlowRepository releasedSubFlowRepository;

    @Autowired
    private ReleasedModelRepository releasedModelRepository;

    @Autowired
    private ReleaseRecordRepository releaseRecordRepository;

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

        List<ReleasedTriggerFlowEntity> releasedTriggerFlowEntities = triggerFlowRepository.findAll().stream().map(entity -> {
            ReleasedTriggerFlowEntity releasedEntity = new ReleasedTriggerFlowEntity();
            BeanUtils.copyProperties(entity, releasedEntity);
            releasedEntity.setId(null);
            releasedEntity.setOriginId(entity.getId());
            releasedEntity.setVersion(requestDTO.getVersion());
            return releasedEntity;
        }).collect(Collectors.toList());

        List<ReleasedSubFlowEntity> releasedSubFlowEntities = subFlowRepository.findAll().stream().map(entity -> {
            ReleasedSubFlowEntity releasedEntity = new ReleasedSubFlowEntity();
            BeanUtils.copyProperties(entity, releasedEntity);
            releasedEntity.setId(null);
            releasedEntity.setOriginId(entity.getId());
            releasedEntity.setVersion(requestDTO.getVersion());
            return releasedEntity;
        }).collect(Collectors.toList());

        List<ReleasedModelEntity> releasedModelFlowEntities = modelRepository.findAll().stream().map(entity -> {
            ReleasedModelEntity releasedEntity = new ReleasedModelEntity();
            BeanUtils.copyProperties(entity, releasedEntity);
            releasedEntity.setId(null);
            releasedEntity.setOriginId(entity.getId());
            releasedEntity.setVersion(requestDTO.getVersion());
            return releasedEntity;
        }).collect(Collectors.toList());

        releasedTriggerFlowRepository.save(releasedTriggerFlowEntities);
        releasedSubFlowRepository.save(releasedSubFlowEntities);
        releasedModelRepository.save(releasedModelFlowEntities);

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
        return releasedTriggerFlowRepository.findByVersion(version).stream().map(flowConvertor::toDTO).collect(Collectors.toList());
    }

    @Override
    public List<ReleasedTriggerFlowEntity> listReleasedTriggerFlow(String version) {
        return releasedTriggerFlowRepository.findByVersion(version);
    }

    @Override
    public List<ReleasedSubFlowEntity> listReleasedSubFlow(String version) {
        return releasedSubFlowRepository.findByVersion(version);
    }

    @Override
    public ReleasedModelEntity getReleasedModel(String version, Long modelId) {
        return releasedModelRepository.findByVersionAndOriginId(version, modelId);
    }

    @Override
    public List<ReleaseRecordDTO> listReleaseRecordDTO() {
        return releaseRecordRepository.findAll(new Sort(Sort.Direction.DESC, "releaseTime"))
                .stream().map(this::toDTO).collect(Collectors.toList());
    }

    private ReleaseRecordDTO toDTO(ReleaseRecordEntity recordEntity) {
        ReleaseRecordDTO dto = new ReleaseRecordDTO();
        BeanUtils.copyProperties(recordEntity, dto);
        return dto;
    }
}
