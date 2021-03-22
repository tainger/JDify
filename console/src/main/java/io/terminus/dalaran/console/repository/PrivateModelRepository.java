package io.terminus.dalaran.console.repository;

import io.terminus.dalaran.console.entity.PrivateModelEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.List;

public interface PrivateModelRepository extends JpaRepository<PrivateModelEntity, Long>, JpaSpecificationExecutor<PrivateModelEntity> {

    List<PrivateModelEntity> findByResourceKey(String resourceKey);
}
