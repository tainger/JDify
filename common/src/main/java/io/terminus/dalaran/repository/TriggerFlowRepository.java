package io.terminus.dalaran.repository;

import io.terminus.dalaran.entity.manage.TriggerFlowEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface TriggerFlowRepository extends JpaRepository<TriggerFlowEntity, Long>, JpaSpecificationExecutor<TriggerFlowEntity> {
}
