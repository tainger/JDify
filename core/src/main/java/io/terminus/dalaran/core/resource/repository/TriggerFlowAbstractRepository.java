package io.terminus.dalaran.core.resource.repository;

import io.terminus.dalaran.core.resource.entity.released.TriggerFlowCoreEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.List;

public interface TriggerFlowAbstractRepository extends JpaRepository<TriggerFlowCoreEntity, Long>, JpaSpecificationExecutor<TriggerFlowCoreEntity> {

    List<TriggerFlowCoreEntity> findByIsExistFalse();

}
