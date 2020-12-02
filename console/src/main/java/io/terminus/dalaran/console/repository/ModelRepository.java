package io.terminus.dalaran.console.repository;

import io.terminus.dalaran.console.entity.ModelEntity;
import io.terminus.dalaran.model.ModelTargetType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.List;

/**
 * Created by jingdi on 2019/3/29
 */
public interface ModelRepository extends JpaRepository<ModelEntity, Long>, JpaSpecificationExecutor<ModelEntity> {

    ModelEntity findByNameAndTargetTypeAndTargetId(String name, ModelTargetType targetType, String targetId);

    ModelEntity findByModuleIdAndModelKey(Long moduleId, String modelKey);

    ModelEntity findByNameAndModuleIdIsNull(String name);

    List<ModelEntity> findByTargetTypeIn(List<ModelTargetType> types);

    List<ModelEntity> findByTargetTypeInAndModuleId(List<ModelTargetType> types, Long moduleId);

    List<ModelEntity> findByModuleId(Long moduleId);

    List<ModelEntity> findByIsExistTrue();

    List<ModelEntity> findByTargetTypeInAndIsExistTrue(List<ModelTargetType> types);

    List<ModelEntity> findByTargetTypeInAndModuleIdAndIsExistTrue(List<ModelTargetType> types, Long moduleId);

    List<ModelEntity> findByModuleIdAndIsExistTrue(Long moduleId);
}
