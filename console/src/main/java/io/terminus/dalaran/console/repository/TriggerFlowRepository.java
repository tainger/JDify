package io.terminus.dalaran.console.repository;

import io.terminus.dalaran.console.entity.TriggerFlowEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface TriggerFlowRepository extends JpaRepository<TriggerFlowEntity, Long>, JpaSpecificationExecutor<TriggerFlowEntity> {
}
