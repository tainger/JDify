package io.terminus.dalaran.core.resource.repository;

import io.terminus.dalaran.core.resource.entity.released.AuthenticatorReleasedEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface AuthenticatorReleasedRepository extends JpaRepository<AuthenticatorReleasedEntity, Long>, JpaSpecificationExecutor<AuthenticatorReleasedEntity> {

    AuthenticatorReleasedEntity findByVersionAndOriginId(String version, String originId);
}
