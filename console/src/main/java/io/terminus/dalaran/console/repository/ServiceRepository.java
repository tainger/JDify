package io.terminus.dalaran.console.repository;

import io.terminus.dalaran.console.entity.ServiceEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.List;

public interface ServiceRepository extends JpaRepository<ServiceEntity, Long>, JpaSpecificationExecutor<ServiceEntity> {

    List<ServiceEntity> findByIsExistTrue();

    List<ServiceEntity> findByModuleIdAndIsExistTrue(String moduleId);

    ServiceEntity findByResourceKey(String resourceKey);
}
