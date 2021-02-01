package io.terminus.dalaran.core.resource.repository;

import io.terminus.dalaran.core.resource.entity.released.TriggerFlowReleasedEntity;
import io.terminus.dalaran.model.flow.FlowStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.List;

public interface TriggerFlowReleasedRepository extends JpaRepository<TriggerFlowReleasedEntity, Long>, JpaSpecificationExecutor<TriggerFlowReleasedEntity> {
    List<TriggerFlowReleasedEntity> findByVersion(String version);

    TriggerFlowReleasedEntity findByVersionAndOriginId(String version, Long triggerFlowId);

    List<TriggerFlowReleasedEntity> findByVersionAndStatusNot(String version, FlowStatus status);

    List<TriggerFlowReleasedEntity> findByVersionAndStatusNotAndTriggerType(String version, FlowStatus status, String triggerType);

    List<TriggerFlowReleasedEntity> findByVersionAndTriggerType(String version, String triggerType);
}
