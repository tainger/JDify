package io.terminus.dalaran.core.resource.repository;

import io.terminus.dalaran.core.resource.entity.released.TriggerFlowReleasedEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.List;

public interface TriggerFlowReleasedRepository extends JpaRepository<TriggerFlowReleasedEntity, Long>, JpaSpecificationExecutor<TriggerFlowReleasedEntity> {
    List<TriggerFlowReleasedEntity> findByVersion(String version);

    TriggerFlowReleasedEntity findByVersionAndOriginId(String version, Long triggerFlowId);
}
