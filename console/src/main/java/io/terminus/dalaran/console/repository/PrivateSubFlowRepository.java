package io.terminus.dalaran.console.repository;

import io.terminus.dalaran.console.entity.PrivateSubFlowEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.List;

public interface PrivateSubFlowRepository extends JpaRepository<PrivateSubFlowEntity, Long>, JpaSpecificationExecutor<PrivateSubFlowEntity> {

    List<PrivateSubFlowEntity> findByResourceKey(String resourceKey);
}
