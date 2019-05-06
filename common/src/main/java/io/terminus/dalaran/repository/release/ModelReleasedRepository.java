package io.terminus.dalaran.repository.release;

import io.terminus.dalaran.entity.release.ModelReleasedEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

/**
 * Created by jingdi on 2019/3/29
 */
public interface ModelReleasedRepository extends JpaRepository<ModelReleasedEntity, Long>, JpaSpecificationExecutor<ModelReleasedEntity> {
    ModelReleasedEntity findByVersionAndOriginId(String version, Long originId);
}
