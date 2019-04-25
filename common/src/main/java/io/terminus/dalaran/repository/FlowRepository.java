package io.terminus.dalaran.repository;

import io.terminus.dalaran.entity.FlowEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface FlowRepository extends JpaRepository<FlowEntity, Long>, JpaSpecificationExecutor<FlowEntity> {
}
