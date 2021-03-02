package io.terminus.dalaran.core.resource.repository;

import io.terminus.dalaran.core.resource.entity.released.LimiterReleasedEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.List;

public interface LimiterReleasedRepository extends JpaRepository<LimiterReleasedEntity, Long>, JpaSpecificationExecutor<LimiterReleasedEntity> {

    LimiterReleasedEntity findByVersionAndOriginId(String version, Long originId);
}
