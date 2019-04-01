package io.terminus.dalaran.console.repository.specification;

import io.terminus.dalaran.console.entity.ProcessorEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

/**
 * Created by jingdi on 2019/3/29
 */
public interface ProcessorQueryRepository extends JpaRepository<ProcessorEntity, Long>, JpaSpecificationExecutor<ProcessorEntity> {
}
