package io.terminus.dalaran.repository.release;

import io.terminus.dalaran.entity.release.ConnectorReleasedEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface ConnectorReleasedRepository extends JpaRepository<ConnectorReleasedEntity, Long>, JpaSpecificationExecutor<ConnectorReleasedEntity> {
    ConnectorReleasedEntity findByVersionAndOriginId(String version, Long originId);
}
