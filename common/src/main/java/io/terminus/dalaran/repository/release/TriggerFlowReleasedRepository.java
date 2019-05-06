package io.terminus.dalaran.repository.release;

import io.terminus.dalaran.entity.release.TriggerFlowReleasedEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.List;

public interface TriggerFlowReleasedRepository extends JpaRepository<TriggerFlowReleasedEntity, Long>, JpaSpecificationExecutor<TriggerFlowReleasedEntity> {
    List<TriggerFlowReleasedEntity> findByVersion(String version);
}
