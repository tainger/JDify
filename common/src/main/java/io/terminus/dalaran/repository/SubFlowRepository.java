package io.terminus.dalaran.repository;

import io.terminus.dalaran.entity.manage.SubFlowEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface SubFlowRepository extends JpaRepository<SubFlowEntity, Long>, JpaSpecificationExecutor<SubFlowEntity> {
}
