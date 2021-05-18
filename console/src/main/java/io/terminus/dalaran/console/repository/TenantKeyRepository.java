package io.terminus.dalaran.console.repository;

import io.terminus.dalaran.console.entity.TenantKeyEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;


public interface TenantKeyRepository extends JpaRepository<TenantKeyEntity, Long>, JpaSpecificationExecutor<TenantKeyEntity> {
}
