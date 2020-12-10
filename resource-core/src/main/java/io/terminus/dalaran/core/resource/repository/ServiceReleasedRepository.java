package io.terminus.dalaran.core.resource.repository;

import io.terminus.dalaran.core.resource.entity.released.ServiceReleasedEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface ServiceReleasedRepository extends JpaRepository<ServiceReleasedEntity, Long>, JpaSpecificationExecutor<ServiceReleasedEntity> {
    ServiceReleasedEntity findByVersionAndOriginId(String version, Long originId);
}
