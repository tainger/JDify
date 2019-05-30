package io.terminus.dalaran.core.resource.repository;

import io.terminus.dalaran.core.resource.entity.released.ConnectorReleasedEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface ConnectorReleasedRepository extends JpaRepository<ConnectorReleasedEntity, Long>, JpaSpecificationExecutor<ConnectorReleasedEntity> {
    ConnectorReleasedEntity findByVersionAndOriginId(String version, Long originId);
}
