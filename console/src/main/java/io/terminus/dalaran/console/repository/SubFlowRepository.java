package io.terminus.dalaran.console.repository;

import io.terminus.dalaran.console.entity.SubFlowEntity;
import io.terminus.dalaran.model.flow.FlowStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.ArrayList;
import java.util.List;

public interface SubFlowRepository extends JpaRepository<SubFlowEntity, Long>, JpaSpecificationExecutor<SubFlowEntity> {

    List<SubFlowEntity> findByIsExistTrue();

    List<SubFlowEntity> findByModuleIdAndIsExistTrue(String moduleId);

    List<SubFlowEntity> findByStatusNotAndIsExistTrue(FlowStatus error);

    SubFlowEntity findByResourceKey(String resourceKey);

    List<SubFlowEntity> findByResourceKeyIn(List<String> resourceKeys);
}
