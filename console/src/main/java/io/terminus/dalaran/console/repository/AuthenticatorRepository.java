package io.terminus.dalaran.console.repository;

import io.terminus.dalaran.console.entity.AuthenticatorEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface AuthenticatorRepository extends JpaRepository<AuthenticatorEntity, Long>, JpaSpecificationExecutor<AuthenticatorEntity> {

    AuthenticatorEntity findByResourceKey(String resourceKey);
}
