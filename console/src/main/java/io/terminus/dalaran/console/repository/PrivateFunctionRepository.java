package io.terminus.dalaran.console.repository;

import io.terminus.dalaran.console.entity.PrivateFunctionEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.List;

public interface PrivateFunctionRepository extends JpaRepository<PrivateFunctionEntity, Long>, JpaSpecificationExecutor<PrivateFunctionEntity> {

    List<PrivateFunctionEntity> findByResourceKey(String resourceKey);
}
