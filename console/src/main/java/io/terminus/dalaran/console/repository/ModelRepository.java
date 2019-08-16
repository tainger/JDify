package io.terminus.dalaran.console.repository;

import io.terminus.dalaran.console.entity.ModelEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.List;

/**
 * Created by jingdi on 2019/3/29
 */
public interface ModelRepository extends JpaRepository<ModelEntity, Long>, JpaSpecificationExecutor<ModelEntity> {

    ModelEntity findByNameAndServiceId(String name, Long serviceId);

    ModelEntity findByNameAndModuleIdIsNull(String name);

    List<ModelEntity> findByHiddenIsNotTrue();
}
