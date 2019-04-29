package io.terminus.dalaran.repository.release;

import io.terminus.dalaran.entity.release.ReleasedModelEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

/**
 * Created by jingdi on 2019/3/29
 */
public interface ReleasedModelRepository extends JpaRepository<ReleasedModelEntity, Long>, JpaSpecificationExecutor<ReleasedModelEntity> {
    ReleasedModelEntity findByVersionAndOriginId(String version, Long originId);
}
