package io.terminus.dalaran.console.repository;

import io.terminus.dalaran.console.entity.AuthenticatorEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.List;

public interface AuthenticatorRepository extends JpaRepository<AuthenticatorEntity, Long>, JpaSpecificationExecutor<AuthenticatorEntity> {

    AuthenticatorEntity findByResourceKey(String resourceKey);

    List<AuthenticatorEntity> findByIsExistTrue();

    List<AuthenticatorEntity> findByResourceKeyIn(List<String> resourceKeys);

    List<AuthenticatorEntity> findByModuleIdAndIsExistTrue(String moduleId);
}
