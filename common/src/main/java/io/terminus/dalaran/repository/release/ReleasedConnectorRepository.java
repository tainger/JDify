package io.terminus.dalaran.repository.release;

import io.terminus.dalaran.entity.release.ReleasedConnectorEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface ReleasedConnectorRepository extends JpaRepository<ReleasedConnectorEntity, Long>, JpaSpecificationExecutor<ReleasedConnectorEntity> {
    ReleasedConnectorEntity findByVersionAndOriginId(String version, Long originId);
}
