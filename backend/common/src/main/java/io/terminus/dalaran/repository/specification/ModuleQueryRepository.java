package io.terminus.dalaran.repository.specification;

import io.terminus.dalaran.entity.ModuleEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

/**
 * Created by jingdi on 2019/4/1
 */
public interface ModuleQueryRepository extends JpaRepository<ModuleEntity, Long>, JpaSpecificationExecutor<ModuleEntity> {
}
