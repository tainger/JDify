package io.terminus.dalaran.console.repository;

import io.terminus.dalaran.console.entity.LimiterEntity;
import io.terminus.dalaran.console.entity.ServiceEntity;
import io.terminus.dalaran.console.entity.TriggerFlowEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.ArrayList;
import java.util.List;

public interface LimiterRepository extends JpaRepository<LimiterEntity, Long>, JpaSpecificationExecutor<LimiterEntity> {

    List<LimiterEntity> findByIsExistTrue();

    LimiterEntity findByResourceKey(String resourceKey);

    List<LimiterEntity> findByResourceKeyIn(ArrayList<String> resourceKeys);
}
