package io.terminus.dalaran.repository.release;

import io.terminus.dalaran.entity.release.ServiceReleasedEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface ServiceReleasedRepository extends JpaRepository<ServiceReleasedEntity, Long>, JpaSpecificationExecutor<ServiceReleasedEntity> {
    ServiceReleasedEntity findByVersionAndOriginId(String version, Long originId);
}
