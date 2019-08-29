package io.terminus.dalaran.console.repository;

import io.terminus.dalaran.console.entity.TriggerFlowEntity;
import io.terminus.dalaran.model.flow.FlowStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.List;

public interface TriggerFlowRepository extends JpaRepository<TriggerFlowEntity, Long>, JpaSpecificationExecutor<TriggerFlowEntity> {

    List<TriggerFlowEntity> findByStatusNotAndTriggerType(FlowStatus status, String triggerType);

    List<TriggerFlowEntity> findByStatusNot(FlowStatus status);
}
