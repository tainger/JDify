package io.terminus.dalaran.console.repository;

import io.terminus.dalaran.console.entity.FunctionEntity;
import io.terminus.dalaran.console.entity.TriggerFlowEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.ArrayList;
import java.util.List;

public interface FunctionRepository extends JpaRepository<FunctionEntity, Long>, JpaSpecificationExecutor<FunctionEntity> {

    List<FunctionEntity> findByIsExistTrue();

    List<FunctionEntity> findByModuleIdAndIsExistTrue(String moduleId);

    FunctionEntity findByResourceKey(String resourceKey);

    List<FunctionEntity> findByResourceKeyIn(List<String> resourceKeys);
}
