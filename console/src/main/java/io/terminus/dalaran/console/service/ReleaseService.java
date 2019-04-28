package io.terminus.dalaran.console.service;

import io.terminus.dalaran.console.model.ReleaseRequestDTO;
import io.terminus.dalaran.console.model.dto.ReleaseRecordDTO;
import io.terminus.dalaran.console.model.dto.flow.TriggerFlowDTO;
import io.terminus.dalaran.entity.release.ReleasedModelEntity;
import io.terminus.dalaran.entity.release.ReleasedSubFlowEntity;
import io.terminus.dalaran.entity.release.ReleasedTriggerFlowEntity;

import java.util.List;

public interface ReleaseService {

    ReleaseRecordDTO release(ReleaseRequestDTO requestDTO);

    ReleaseRecordDTO rollback(String version);

    List<TriggerFlowDTO> listReleasedTriggerFlowDTO(String version);

    List<ReleasedTriggerFlowEntity> listReleasedTriggerFlow(String version);

    List<ReleasedSubFlowEntity> listReleasedSubFlow(String version);

    ReleasedModelEntity getReleasedModel(String version, Long modelId);

    List<ReleaseRecordDTO> listReleaseRecordDTO();
}
