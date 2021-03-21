package io.terminus.dalaran.console.repository;

import io.terminus.dalaran.console.entity.PrivateServiceEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.List;

public interface PrivateServiceRepository extends JpaRepository<PrivateServiceEntity, Long>, JpaSpecificationExecutor<PrivateServiceEntity> {

    List<PrivateServiceEntity> findByResourceKey(String resourceKey);
}
