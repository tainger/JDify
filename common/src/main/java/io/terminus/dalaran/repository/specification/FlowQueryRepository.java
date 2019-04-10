package io.terminus.dalaran.repository.specification;

import io.terminus.dalaran.entity.FlowEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

/**
 * Created by jingdi on 2019/3/29
 */
public interface FlowQueryRepository extends JpaRepository<FlowEntity, Long>, JpaSpecificationExecutor<FlowEntity> {
}
