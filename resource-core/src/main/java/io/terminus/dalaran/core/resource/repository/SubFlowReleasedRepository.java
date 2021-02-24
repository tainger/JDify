package io.terminus.dalaran.core.resource.repository;

import io.terminus.dalaran.core.resource.entity.released.SubFlowReleasedEntity;
import io.terminus.dalaran.model.flow.FlowStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.List;

public interface SubFlowReleasedRepository extends JpaRepository<SubFlowReleasedEntity, Long>, JpaSpecificationExecutor<SubFlowReleasedEntity> {
    List<SubFlowReleasedEntity> findByVersion(String version);

    List<SubFlowReleasedEntity> findByVersionAndStatusNot(String version, FlowStatus status);

    List<SubFlowReleasedEntity> findByVersionAndStatusNotAndIsOnlineTrue(String version, FlowStatus status);

    SubFlowReleasedEntity findByVersionAndOriginId(String version, Long subFlowId);
}
