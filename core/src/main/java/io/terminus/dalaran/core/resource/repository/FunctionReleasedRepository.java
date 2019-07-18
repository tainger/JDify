package io.terminus.dalaran.core.resource.repository;

import io.terminus.dalaran.core.resource.entity.released.FunctionReleasedEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.List;

public interface FunctionReleasedRepository extends JpaRepository<FunctionReleasedEntity, Long>, JpaSpecificationExecutor<FunctionReleasedEntity> {
    List<FunctionReleasedEntity> findByVersion(String version);

    FunctionReleasedEntity findByVersionAndOriginId(String version, Long originId);
}
