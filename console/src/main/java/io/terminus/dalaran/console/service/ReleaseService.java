package io.terminus.dalaran.console.service;

import io.terminus.dalaran.core.resource.entity.released.ModelReleasedEntity;
import io.terminus.dalaran.core.resource.entity.released.PropertyReleasedEntity;
import io.terminus.dalaran.core.resource.entity.released.SubFlowReleasedEntity;
import io.terminus.dalaran.core.resource.entity.released.TriggerFlowReleasedEntity;
import io.terminus.dalaran.model.dto.ReleaseRecordDTO;
import io.terminus.dalaran.model.dto.ReleaseRequestDTO;
import io.terminus.dalaran.model.dto.flow.TriggerFlowDTO;

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
