package io.terminus.dalaran.repository;

import io.terminus.dalaran.entity.StructureEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

/**
 * Created by jingdi on 2019/3/29
 */
public interface StructureRepository extends JpaRepository<StructureEntity, Long>, JpaSpecificationExecutor<StructureEntity> {
}
