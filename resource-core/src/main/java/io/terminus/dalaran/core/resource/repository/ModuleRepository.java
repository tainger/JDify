package io.terminus.dalaran.core.resource.repository;

import io.terminus.dalaran.core.resource.entity.common.ModuleEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.List;

/**
 * Created by jingdi on 2019/4/1
 */
public interface ModuleRepository extends JpaRepository<ModuleEntity, Long>, JpaSpecificationExecutor<ModuleEntity> {

    List<ModuleEntity> findByIsExistTrue();

    ModuleEntity findByIdAndIsExistTrue(Long id);

    ModuleEntity findByResourceKey(String resourceKey);
}
