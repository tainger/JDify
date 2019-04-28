package io.terminus.dalaran.repository;

import io.terminus.dalaran.entity.release.ReleasedTriggerFlowEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.List;

public interface ReleasedTriggerFlowRepository extends JpaRepository<ReleasedTriggerFlowEntity, Long>, JpaSpecificationExecutor<ReleasedTriggerFlowEntity> {
    List<ReleasedTriggerFlowEntity> findByVersion(String version);
}
