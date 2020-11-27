package io.terminus.dalaran.console.repository;

import io.terminus.dalaran.console.entity.SubFlowEntity;
import io.terminus.dalaran.model.flow.FlowStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.List;

public interface SubFlowRepository extends JpaRepository<SubFlowEntity, Long>, JpaSpecificationExecutor<SubFlowEntity> {

    List<SubFlowEntity> findByStatusNot(FlowStatus error);

    List<SubFlowEntity> findByIsExistTrue();
}
