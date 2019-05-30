package io.terminus.dalaran.core.resource.repository;

import io.terminus.dalaran.core.resource.entity.released.ModelReleasedEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

/**
 * Created by jingdi on 2019/3/29
 */
public interface ModelReleasedRepository extends JpaRepository<ModelReleasedEntity, Long>, JpaSpecificationExecutor<ModelReleasedEntity> {
    ModelReleasedEntity findByVersionAndOriginId(String version, Long originId);
}
