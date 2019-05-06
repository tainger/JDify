package io.terminus.dalaran.console.service;

import io.terminus.dalaran.console.model.ReleaseRequestDTO;
import io.terminus.dalaran.console.model.dto.ReleaseRecordDTO;
import io.terminus.dalaran.console.model.dto.flow.TriggerFlowDTO;
import io.terminus.dalaran.entity.release.ModelReleasedEntity;
import io.terminus.dalaran.entity.release.PropertyReleasedEntity;
import io.terminus.dalaran.entity.release.SubFlowReleasedEntity;
import io.terminus.dalaran.entity.release.TriggerFlowReleasedEntity;

import java.util.List;

public interface ReleaseService {

    ReleaseRecordDTO release(ReleaseRequestDTO requestDTO);

    ReleaseRecordDTO rollback(String version);

    List<TriggerFlowDTO> listReleasedTriggerFlowDTO(String version);

    List<TriggerFlowReleasedEntity> listReleasedTriggerFlow(String version);

    List<SubFlowReleasedEntity> listReleasedSubFlow(String version);

    ModelReleasedEntity getReleasedModel(String version, Long modelId);

    List<PropertyReleasedEntity> getReleasedProperty(String version);

    List<ReleaseRecordDTO> listReleaseRecordDTO();
}
