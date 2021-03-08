package io.terminus.dalaran.console.repository;

import io.terminus.dalaran.console.entity.TriggerFlowEntity;
import io.terminus.dalaran.model.flow.FlowStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.List;

public interface TriggerFlowRepository extends JpaRepository<TriggerFlowEntity, Long>, JpaSpecificationExecutor<TriggerFlowEntity> {

    List<TriggerFlowEntity> findByStatusNotAndIsExistTrue(FlowStatus status);

    TriggerFlowEntity findByName(String name);

    List<TriggerFlowEntity> findByModuleIdAndIsExistTrue(String moduleId);

    List<TriggerFlowEntity> findByIsExistTrue();

    List<TriggerFlowEntity> findByStatusNotAndTriggerTypeAndIsExistTrue(FlowStatus status, String triggerType);

    TriggerFlowEntity findByResourceKey(String resourceKey);
}
